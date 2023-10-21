package com.abhi.currency.networks;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<JsonElement> getCurrencies(@Url String url);

    @GET
    Call<JsonElement> getExchangeRates(@Url String url);
}
