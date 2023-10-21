package com.abhi.currency.models;

public class CurrencyRate {
    private double rate = 0;
    private String currencyCode = "";
    public CurrencyRate nextNode = null;

    public CurrencyRate(double rate, String currencyCode) {
        this.rate = rate;
        this.currencyCode = currencyCode;
    }

    public double getRate() {
        return rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
