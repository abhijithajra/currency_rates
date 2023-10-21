package com.abhi.currency.networks;

import androidx.annotation.NonNull;


import com.abhi.currency.utils.Utils;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    @NonNull
    public static ApiInterface getAPIInterface() {
        final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .connectTimeout(180, TimeUnit.SECONDS)
                .build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(ApiInterface.class);
    }

    public interface OnReceive {
        void onComplete(final String response);
    }

    public static void doApiCall(final @NonNull Call<JsonElement> mAPIInterface, final OnReceive listener) {
        try {
            mAPIInterface.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull retrofit2.Response<JsonElement> response) {
                    System.out.println("!--!-$!OUTPUT: " + response);
                    JSONObject data = new JSONObject();
                    try {
                        if (response.body() != null && response.body().toString().length() > 0) {
                            data = new JSONObject(response.body().toString());
                            if (response.code() != 200) {
                                data.put("error", true);
                                data.put("message", response.message());
                            }
                        } else if (response.code() != 200) {
                            data.put("error", true);
                            data.put("message", response.message());
                        }
                    } catch (Exception e) {
                        Utils.handledException(e);
                    } finally {
                        System.out.println("!--!-$!RESPONSE : " + response.body());
                        if (listener != null) {
                            listener.onComplete(response.body() != null && response.body().toString().trim().length() > 0 ? data.toString() : null);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    if (listener != null) listener.onComplete(null);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onComplete(null);
            Utils.handledException(e);
        }
    }
}
