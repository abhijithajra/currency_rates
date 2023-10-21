package com.abhi.currency.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.currency.databinding.CurrencyItemBinding;
import com.abhi.currency.models.CurrencyRate;
import com.abhi.currency.utils.Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;


public class CurrencyRateListAdapter extends RecyclerView.Adapter<CurrencyRateListAdapter.ViewHolder> {

    private final LinkedList<CurrencyRate> mCurrencyList = new LinkedList<>();
    private final NumberFormat mNumberFormatter;

    public CurrencyRateListAdapter() {
        mNumberFormatter = new DecimalFormat("0.000000000000");
    }

    public LinkedList<CurrencyRate> getCurrencyList() {
        return mCurrencyList;
    }

    public void updateDataSetChange(LinkedList<CurrencyRate> currencyModels) {
        try {
            mCurrencyList.clear();
            if (currencyModels != null) mCurrencyList.addAll(currencyModels);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Utils.handledException(e);
                    }
                }
            });
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CurrencyItemBinding itemBinding = CurrencyItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        try {
            final int position = holder.getAdapterPosition();
            CurrencyRate currency = mCurrencyList.get(position);
            if (currency != null) {
                holder.mItemBinding.tvCurrencyName.setText(currency.getCurrencyCode());
                long rate = Math.round(currency.getRate());
                holder.mItemBinding.tvCountryName.setText((currency.getRate() != rate) ? BigDecimal.valueOf(currency.getRate()).toPlainString() : Long.toString(rate));
            }
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }

    @Override
    public int getItemCount() {
        return mCurrencyList.size();
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CurrencyItemBinding mItemBinding;

        public ViewHolder(@NonNull CurrencyItemBinding itemView) {
            super(itemView.getRoot());
            mItemBinding = itemView;
        }
    }
}
