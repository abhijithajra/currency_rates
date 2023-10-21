package com.abhi.currency.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abhi.currency.R;
import com.abhi.currency.adapters.CurrencyRateListAdapter;
import com.abhi.currency.databinding.ActivityCurrencyRatesBinding;
import com.abhi.currency.models.CurrencyRate;
import com.abhi.currency.networks.Constants;
import com.abhi.currency.networks.RetrofitClient;
import com.abhi.currency.utils.BubbleSortManager;
import com.abhi.currency.utils.IntentKey;
import com.abhi.currency.utils.OnTextChangeListener;
import com.abhi.currency.utils.Utils;

import org.json.JSONObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;

public class CurrencyRatesActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityCurrencyRatesBinding mBinding;
    private CurrencyRateListAdapter mAdapter = null;
    private String selectedCurrencyCode = null;
    private boolean isInitialized = true;
    private int mSortBtnId = 0;
    private final LinkedList<CurrencyRate> mCurrencyRateList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_currency_rates);
        selectedCurrencyCode = getIntent().getStringExtra(IntentKey.selected_currency_code.name());
        mBinding.llCode.setOnClickListener(this);
        mBinding.llCurrencyName.setOnClickListener(this);
        mBinding.ivBackBtn.setOnClickListener(this);
        mBinding.ivClearBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        if (isInitialized) {
            isInitialized = false;
            setupRecyclerView();
            fetchCurrencies();
            mBinding.etSearch.addTextChangedListener(new OnTextChangeListener(700) {
                @Override
                public void onChange(String mInputValue) {
                    try {
                        final LinkedList<CurrencyRate> filterList = new LinkedList<>();
                        if (Utils.isNotEmpty(mInputValue)) {
                            for (CurrencyRate currency : mCurrencyRateList) {
                                if (currency.getCurrencyCode().toLowerCase().contains(mInputValue.toLowerCase())) {
                                    filterList.add(currency);
                                }
                            }
                        } else {
                            filterList.addAll(mCurrencyRateList);
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
            mAdapter = new CurrencyRateListAdapter();
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
            mCurrencyRateList.clear();
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    RetrofitClient.doApiCall(RetrofitClient.getAPIInterface().getExchangeRates(Constants.FETCH_EXCHANGE_RATES + selectedCurrencyCode + ".json"), new RetrofitClient.OnReceive() {
                        @Override
                        public void onComplete(String response) {
                            String showingDate = "";
                            try {
                                if (response == null) {
                                    Toast.makeText(CurrencyRatesActivity.this, "Something went wrong! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                                } else {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    showingDate = jsonObject.optString("date", "");
                                    final JSONObject data = jsonObject.optJSONObject(selectedCurrencyCode);
                                    if (data != null && data.length() > 0) {
                                        final Iterator<String> keys = data.keys();
                                        while (keys.hasNext()) {
                                            final String currencyCode = keys.next();
                                            if (Utils.isNotEmpty(currencyCode)) {
                                                final String currencyRate = data.optString(currencyCode);
                                                if (Utils.isNotEmpty(currencyRate)) {
                                                    mCurrencyRateList.add(new CurrencyRate(data.optDouble(currencyCode, 0), currencyCode));
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Utils.handledException(e);
                            } finally {
                                if (mAdapter != null) {
                                    mAdapter.updateDataSetChange(mCurrencyRateList);
                                }
                                String finalShowingDate = showingDate;
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mBinding.ivNoDatFound.setVisibility(mCurrencyRateList.size() > 0 ? View.GONE : View.VISIBLE);
                                            mBinding.progressBar.setVisibility(View.GONE);
                                            showHeader(finalShowingDate);
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

    private void showHeader(String showingDate) {
        try {
            final SpannableStringBuilder mHeaderText = new SpannableStringBuilder();
            mHeaderText.append("CURRENCY RATES");
            mHeaderText.append("\n");
            int length = mHeaderText.length();
            mHeaderText.setSpan(new StyleSpan(Typeface.BOLD), 0, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHeaderText.append("Below showing the rates of 1 ").append(selectedCurrencyCode.toUpperCase());
            if (Utils.isNotEmpty(showingDate)) {
                mHeaderText.append(" of ").append("dated ").append(showingDate);
            }
            mHeaderText.setSpan(new RelativeSizeSpan(0.6f), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHeaderText.setSpan(new ForegroundColorSpan(Color.LTGRAY), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHeaderText.setSpan(new StyleSpan(Typeface.NORMAL), length, mHeaderText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBinding.tvHeaderText.setText(mHeaderText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.ivBackBtn) {
                onBackPressed();
            } else if (view.getId() == R.id.ivClearBtn) {
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
                if (mAdapter != null) {
                    mAdapter.getCurrencyList().sort(new CurrencyCodeComparator(mSortBtnId));
                    mAdapter.notifyDataSetChanged();
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

                final BubbleSortManager mSortManager = new BubbleSortManager(mAdapter.getCurrencyList());
                mSortManager.sort(mSortBtnId == 2);
                if (mAdapter != null) {
                    mAdapter.updateDataSetChange(mSortManager.getList());
                }
            }
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }

    public static class CurrencyCodeComparator implements Comparator<CurrencyRate> {
        private final int mSortBtnId;

        public CurrencyCodeComparator(int mSortBtnId) {
            this.mSortBtnId = mSortBtnId;
        }

        @Override
        public int compare(CurrencyRate s1, CurrencyRate s2) {
            switch (mSortBtnId) {
                case 0:
                    return s1.getCurrencyCode().trim().toLowerCase().compareTo(s2.getCurrencyCode().trim().toLowerCase());
                case 1:
                    return s2.getCurrencyCode().trim().toLowerCase().compareTo(s1.getCurrencyCode().trim().toLowerCase());
                default:
                    return 0;
            }
        }
    }
}