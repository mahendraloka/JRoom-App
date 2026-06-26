package com.example.roombook;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.roombook.api.ApiClient;
import com.example.roombook.api.ApiService;
import com.example.roombook.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatBookingActivity extends AppCompatActivity {

    private LinearLayout layoutRiwayat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_booking);

        layoutRiwayat = findViewById(R.id.layoutRiwayat);

        ambilRiwayatUser();
    }

    private void ambilRiwayatUser() {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getApiService();
        Map<String, Object> request = new HashMap<>();
        request.put("action", "get_riwayat_by_email");
        request.put("email", email);

        api.getRiwayatByEmail(request).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> daftar = response.body();
                    if (daftar.isEmpty()) {
                        TextView kosong = new TextView(RiwayatBookingActivity.this);
                        kosong.setText("Belum ada riwayat booking.");
                        kosong.setTextColor(Color.LTGRAY);
                        layoutRiwayat.addView(kosong);
                        return;
                    }

                    for (Booking booking : daftar) {
                        CardView card = new CardView(RiwayatBookingActivity.this);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        cardParams.setMargins(0, 0, 0, 32);
                        card.setLayoutParams(cardParams);
                        card.setCardElevation(8f);
                        card.setRadius(20f);
                        card.setUseCompatPadding(true);
                        card.setCardBackgroundColor(Color.WHITE);

                        LinearLayout isi = new LinearLayout(RiwayatBookingActivity.this);
                        isi.setOrientation(LinearLayout.VERTICAL);
                        isi.setPadding(24, 24, 24, 24);

                        TextView tanggal = new TextView(RiwayatBookingActivity.this);
                        tanggal.setText("📅 " + booking.getTanggal() + " | " + booking.getWaktuMulai() + " - " + booking.getWaktuSelesai());
                        tanggal.setTextColor(Color.parseColor("#000000"));
                        tanggal.setTypeface(null, Typeface.BOLD);

                        TextView ruangan = new TextView(RiwayatBookingActivity.this);
                        ruangan.setText("🏫 Ruangan: " + booking.getNama());
                        ruangan.setTextColor(Color.parseColor("#000000"));

                        TextView pemesan = new TextView(RiwayatBookingActivity.this);
                        pemesan.setText("👤 Pemesan: " + booking.getPemesan());
                        pemesan.setTextColor(Color.parseColor("#000000"));

                        TextView tujuan = new TextView(RiwayatBookingActivity.this);
                        tujuan.setText("🎯 Tujuan: " + booking.getTujuan());
                        tujuan.setTextColor(Color.parseColor("#000000"));

                        isi.addView(tanggal);
                        isi.addView(ruangan);
                        isi.addView(pemesan);
                        isi.addView(tujuan);

                        card.addView(isi);
                        layoutRiwayat.addView(card);
                    }

                } else {
                    Toast.makeText(RiwayatBookingActivity.this, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(RiwayatBookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
