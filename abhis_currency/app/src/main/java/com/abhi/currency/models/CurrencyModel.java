package com.abhi.currency.models;

public class CurrencyModel {
    private final String currencyCode, currencyName;

    public CurrencyModel(String currencyCode, String currencyName) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
