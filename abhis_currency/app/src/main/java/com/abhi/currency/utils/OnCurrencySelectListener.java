package com.abhi.currency.utils;

import androidx.annotation.NonNull;

public interface OnCurrencySelectListener {
    void doNext(@NonNull final String selectedCurrencyCode);
}
