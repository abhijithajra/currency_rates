package com.abhi.currency.adapters;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.currency.activities.CurrencyRatesActivity;
import com.abhi.currency.databinding.CurrencyItemBinding;
import com.abhi.currency.models.CurrencyModel;
import com.abhi.currency.utils.OnCurrencySelectListener;
import com.abhi.currency.utils.Utils;

import java.util.LinkedList;


public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {

    private final LinkedList<CurrencyModel> mCurrencyList = new LinkedList<>();
    private final OnCurrencySelectListener mOnCurrencySelectListener;

    public CurrencyListAdapter(OnCurrencySelectListener listener) {
        this.mOnCurrencySelectListener = listener;
    }

    public LinkedList<CurrencyModel> getCurrencyList() {
        return mCurrencyList;
    }

    public void updateDataSetChange(LinkedList<CurrencyModel> currencyModels) {
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
            CurrencyModel currency = mCurrencyList.get(position);
            if (currency != null) {
                holder.mItemBinding.tvCurrencyName.setText(currency.getCurrencyCode());
                holder.mItemBinding.tvCountryName.setText(currency.getCurrencyName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnCurrencySelectListener.doNext(currency.getCurrencyCode());
                    }
                });
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
            mItemBinding.ivRightArrow.setVisibility(View.VISIBLE);
        }
    }
}
