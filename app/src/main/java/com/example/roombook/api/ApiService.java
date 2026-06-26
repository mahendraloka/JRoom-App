package com.example.roombook.api;

import com.example.roombook.model.Booking;
import com.example.roombook.model.Ruangan;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

public interface ApiService {
    @GET("api.php")
    Call<List<Ruangan>> getDaftarRuangan();

    @POST("api.php")
    Call<JsonObject> cekKetersediaan(@Body Map<String, Object> data);

    @POST("api.php")
    Call<JsonObject> tambahBooking(@Body JsonObject data);


    @POST("api.php")
    Call<Map<String, String>> tambahBooking(@Body Map<String, Object> data);

    @GET("check_availability")
    Call<Boolean> getKetersediaan(@Query("ruangan_id") int ruanganId, @Query("tanggal") String tanggal);

    @POST("api.php")
    Call<List<Booking>> getBookingByRuanganId(@Body Map<String, Object> data);

    @POST("api.php")
    Call<JsonObject> loginUser(@Body Map<String, String> data);

    @POST("api.php")
    Call<JsonObject> registerUser(@Body Map<String, String> data);

    @GET("api")
    Call<List<JsonObject>> getHariLibur();

    @POST("api.php")
    Call<List<Booking>> getRiwayatByEmail(@Body Map<String, Object> body);


}