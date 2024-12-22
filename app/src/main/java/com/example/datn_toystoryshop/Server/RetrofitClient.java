package com.example.datn_toystoryshop.Server;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    //chỉ cần nhập IPv4 trong APIService
    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(APIService.BASE_URL) // Sử dụng BASE_URL từ APIService
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitPayment() {
        return new Retrofit.Builder().baseUrl(APIService.PAYMENT_SERVER_URL) // Sử dụng BASE_URL từ APIService
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static APIService getAPIService() {
        return getInstance().create(APIService.class);
    }

    public static APIService getAPIServicePayment() {
        return getRetrofitPayment().create(APIService.class);
    }
}
