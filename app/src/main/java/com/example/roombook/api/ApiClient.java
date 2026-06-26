package com.example.roombook.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // 🔧 API lokal Anda (RoomBook)
    private static final String BASE_URL = "http://192.168.100.9/roombook/";
    private static Retrofit retrofit = null;

    // 🔧 API Hari Libur Nasional
    private static final String LIBUR_URL = "https://api-harilibur.vercel.app/";
    private static Retrofit retrofitLibur = null;

    // ✅ Untuk API RoomBook
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // ✅ Untuk API Hari Libur Nasional
    public static ApiService getApiServiceLibur() {
        if (retrofitLibur == null) {
            retrofitLibur = new Retrofit.Builder()
                    .baseUrl(LIBUR_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitLibur.create(ApiService.class);
    }
}
