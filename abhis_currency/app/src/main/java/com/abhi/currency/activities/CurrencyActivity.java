package com.abhi.currency.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abhi.currency.R;
import com.abhi.currency.adapters.CurrencyListAdapter;
import com.abhi.currency.databinding.ActivityCurrencyBinding;
import com.abhi.currency.models.CurrencyModel;
import com.abhi.currency.networks.Constants;
import com.abhi.currency.networks.RetrofitClient;
import com.abhi.currency.utils.IntentKey;
import com.abhi.currency.utils.OnCurrencySelectListener;
import com.abhi.currency.utils.OnTextChangeListener;
import com.abhi.currency.utils.Utils;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;

public class CurrencyActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCurrencyBinding mBinding = null;
    private CurrencyListAdapter mAdapter = null;
    private boolean isInitialized = true;
    private final LinkedList<CurrencyModel> mCurrencyList = new LinkedList<>();
    private int mSortBtnId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_currency);
        mBinding.llCode.setOnClickListener(this);
        mBinding.llCurrencyName.setOnClickListener(this);
        mBinding.ivClearBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        if (isInitialized) {
            isInitialized = false;
            setupRecyclerView();
            fetchCurrencies();
            try {
                final SpannableStringBuilder mHeaderText = new SpannableStringBuilder();
                mHeaderText.append("CURRENCIES");
                mHeaderText.append("\n");
                int length = mHeaderText.length();
                mHeaderText.setSpan(new StyleSpan(Typeface.BOLD), 0, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mHeaderText.append("Tap on the currency to check rates");
                mHeaderText.setSpan(new RelativeSizeSpan(0.6f), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mHeaderText.setSpan(new ForegroundColorSpan(Color.LTGRAY), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mHeaderText.setSpan(new StyleSpan(Typeface.NORMAL), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mBinding.tvHeaderText.setText(mHeaderText);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mBinding.etSearch.addTextChangedListener(new OnTextChangeListener(700) {
                @Override
                public void onChange(String mInputValue) {
                    try {
                        final LinkedList<CurrencyModel> filterList = new LinkedList<>();
                        if (Utils.isNotEmpty(mInputValue)) {
                            for (CurrencyModel currency : mCurrencyList) {
                                if (currency.getCurrencyName().toLowerCase().contains(mInputValue.toLowerCase()) || currency.getCurrencyCode().toLowerCase().contains(mInputValue.toLowerCase())) {
                                    filterList.add(currency);
                                }
                            }
                        } else {
                            filterList.addAll(mCurrencyList);
                        }
                        mAdapter.updateDataSetChange(filterList);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mBinding.ivClearBtn.setVisibility(Utils.isNotEmpty(mBinding.etSearch.getText().toString().trim()) ? View.VISIBLE : View.GONE);
                                    mBinding.ivNoDatFound.setVisibility((filterList.size() > 0) ? View.GONE : View.VISIBLE);
                                } catch (Exception e) {
                                    Utils.handledException(e);
                                }
                            }
                        });
                    } catch (Exception e) {
                        Utils.handledException(e);
                    }
                }
            });
        }
        super.onStart();
    }

    private void setupRecyclerView() {
        try {
            mAdapter = new CurrencyListAdapter(new OnCurrencySelectListener() {
                @Override
                public void doNext(@NonNull String selectedCurrencyCode) {
                    Intent mIntent = new Intent(CurrencyActivity.this, CurrencyRatesActivity.class);
                    mIntent.putExtra(IntentKey.selected_currency_code.name(), selectedCurrencyCode);
                    startActivity(mIntent);
                }
            });
            mBinding.rvCurrencyList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mBinding.rvCurrencyList.setHasFixedSize(true);
            mBinding.rvCurrencyList.setNestedScrollingEnabled(false);
            mBinding.ivNoDatFound.setVisibility(View.GONE);
            mBinding.rvCurrencyList.setAdapter(mAdapter);
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }

    private void fetchCurrencies() {
        if (Utils.isConnected(this)) {
            mCurrencyList.clear();
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    RetrofitClient.doApiCall(RetrofitClient.getAPIInterface().getCurrencies(Constants.FETCH_CURRENCIES), new RetrofitClient.OnReceive() {
                        @Override
                        public void onComplete(String response) {
                            try {
                                if (response == null) {
                                    Toast.makeText(CurrencyActivity.this, "Something went wrong! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                                } else {
                                    final JSONObject data = new JSONObject(response);
                                    final Iterator<String> keys = data.keys();
                                    while (keys.hasNext()) {
                                        final String currencyCode = keys.next();
                                        if (Utils.isNotEmpty(currencyCode)) {
                                            final String currencyName = data.optString(currencyCode);
                                            if (Utils.isNotEmpty(currencyName)) {
                                                final CurrencyModel currencyModel = new CurrencyModel(currencyCode, currencyName);
                                                mCurrencyList.add(currencyModel);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Utils.handledException(e);
                            } finally {
                                if (mAdapter != null) {
                                    mAdapter.updateDataSetChange(mCurrencyList);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mBinding.progressBar.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                            Utils.handledException(e);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    Utils.handledException(e);
                }
            });
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
            mBinding.ivNoDatFound.setVisibility(View.VISIBLE);
            Utils.showNoInternetDialog(this, "No Internet Connection", "Please check your internet connection and try again!", false);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.ivClearBtn) {
                mBinding.etSearch.setText("");
            } else if (view.getId() == R.id.llCode) {
                mBinding.ivSortCurrencyName.setVisibility(View.INVISIBLE);
                mBinding.ivSortCode.setVisibility(View.VISIBLE);
                if (mSortBtnId == 0) {
                    mBinding.ivSortCode.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    mSortBtnId = 1;
                } else {
                    mBinding.ivSortCode.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    mSortBtnId = 0;
                }
            } else if (view.getId() == R.id.llCurrencyName) {
                mBinding.ivSortCode.setVisibility(View.INVISIBLE);
                mBinding.ivSortCurrencyName.setVisibility(View.VISIBLE);
                if (mSortBtnId == 2) {
                    mBinding.ivSortCurrencyName.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    mSortBtnId = 3;
                } else {
                    mBinding.ivSortCurrencyName.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    mSortBtnId = 2;
                }
            }
            if (mAdapter != null) {
                mAdapter.getCurrencyList().sort(new CurrencyCodeComparator(mSortBtnId));
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }

    public static class CurrencyCodeComparator implements Comparator<CurrencyModel> {
        private final int mSortBtnId;

        public CurrencyCodeComparator(int mSortBtnId) {
            this.mSortBtnId = mSortBtnId;
        }

        @Override
        public int compare(CurrencyModel s1, CurrencyModel s2) {
            switch (mSortBtnId) {
                case 0:
                    return s1.getCurrencyCode().trim().toLowerCase().compareTo(s2.getCurrencyCode().trim().toLowerCase());
                case 1:
                    return s2.getCurrencyCode().trim().toLowerCase().compareTo(s1.getCurrencyCode().trim().toLowerCase());
                case 2:
                    return s1.getCurrencyName().trim().toLowerCase().compareTo(s2.getCurrencyName().trim().toLowerCase());
                case 3:
                    return s2.getCurrencyName().trim().toLowerCase().compareTo(s1.getCurrencyName().trim().toLowerCase());
                default:
                    return 0;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Utils.showNoInternetDialog(this, "Warning", "Are you want to close Abhi's Currency?", true);
    }
}