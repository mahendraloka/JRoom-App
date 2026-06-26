package com.example.roombook;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roombook.api.ApiClient;
import com.example.roombook.api.ApiService;
import com.example.roombook.model.Booking;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatDelegate;



public class RoomStatusActivity extends AppCompatActivity {

    private TextView tvStatusTanggal;

    private CalendarView calendarView;
    private TextView tvNamaRuangan;
    private LinearLayout layoutBookingList;

    private int ruanganId;
    private String ruanganNama;

    private final Set<String> bookedDates = new HashSet<>();
    private final Set<String> tanggalLibur = new HashSet<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private List<Booking> allBookings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_status);

        tvNamaRuangan = findViewById(R.id.tvNamaRuangan);
        calendarView = findViewById(R.id.calendarView);
        calendarView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            ViewGroup calendarChild = (ViewGroup) calendarView.getChildAt(0);
            if (calendarChild != null) {
                setTextColorDeep(calendarChild, Color.BLACK);
            }
        });
        layoutBookingList = findViewById(R.id.layoutBookingList);

        TextView tvInfoTambahan = findViewById(R.id.tvInfoTambahan);
        ruanganId = getIntent().getIntExtra("ruangan_id", -1);
        ruanganNama = getIntent().getStringExtra("ruangan_nama");
        int kapasitas = getIntent().getIntExtra("ruangan_kapasitas", 0);
        int lantai = getIntent().getIntExtra("ruangan_lantai", 0);
        tvInfoTambahan.setText("Lantai: " + lantai + " | Kapasitas: " + kapasitas + " orang");
        tvNamaRuangan.setText("Status Booking: " + ruanganNama);
        tvStatusTanggal = findViewById(R.id.tvStatusTanggal);
        ambilHariLibur();
        ambilDataBooking();
    }

    private void ambilHariLibur() {
        ApiService apiLibur = ApiClient.getApiServiceLibur();
        apiLibur.getHariLibur().enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (JsonObject libur : response.body()) {
                        if (libur.get("is_national_holiday").getAsBoolean()) {
                            tanggalLibur.add(libur.get("holiday_date").getAsString());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                Toast.makeText(RoomStatusActivity.this, "Gagal memuat hari libur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ambilDataBooking() {
        ApiService api = ApiClient.getApiService();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("action", "get_booking_by_ruangan");
        requestData.put("ruangan_id", ruanganId);

        api.getBookingByRuanganId(requestData).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allBookings = response.body();
                    for (Booking booking : allBookings) {
                        bookedDates.add(booking.getTanggal());
                    }

                    tampilkanBookingDetail(allBookings);

                    calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);
                        String selectedDate = sdf.format(selected.getTime());

                        boolean booked = bookedDates.contains(selectedDate);
                        boolean isLibur = tanggalLibur.contains(selectedDate);
                        boolean isSunday = selected.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

                        String status = (booked ? "❌ " : "✅ ") +
                                ruanganNama + (booked ? " sudah dibooking" : " tersedia") + " pada " + selectedDate;

                        if (isLibur) {
                            status += "\n💼 Tanggal ini adalah hari libur nasional";
                        }

                        if (isSunday) {
                            status += "\n🔴 Hari Minggu (libur akhir pekan)";
                        }

                        tvStatusTanggal.setText(status);
                        tampilkanBookingPerTanggal(selectedDate);
                    });
                } else {
                    tvNamaRuangan.setText("Gagal memuat data booking.");
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                tvNamaRuangan.setText("Kesalahan jaringan: " + t.getMessage());
            }
        });
    }

    private void setTextColorDeep(View view, int color) {
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTextColorDeep(group.getChildAt(i), color);
            }
        }
    }



    private void tampilkanBookingPerTanggal(String tanggal) {
        layoutBookingList.removeAllViews();
        List<Booking> filtered = new ArrayList<>();
        for (Booking booking : allBookings) {
            if (tanggal.equals(booking.getTanggal())) {
                filtered.add(booking);
            }
        }
        tampilkanBookingDetail(filtered);
    }

    private void tampilkanBookingDetail(List<Booking> bookings) {
        layoutBookingList.removeAllViews();
        if (bookings.isEmpty()) {
            TextView kosong = new TextView(this);
            kosong.setText("Tidak ada booking ruangan.");
            kosong.setPadding(16, 16, 16, 16);
            kosong.setBackgroundResource(R.drawable.bg_card_booking);
            layoutBookingList.addView(kosong);
            return;
        }

        for (Booking booking : bookings) {
            TextView tv = new TextView(this);
            String info = "📅 " + booking.getTanggal() +
                    " | " + booking.getWaktuMulai() + " - " + booking.getWaktuSelesai() +
                    " | oleh: " + booking.getPemesan();
            tv.setText(info);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setTextSize(14);
            tv.setPadding(24, 24, 24, 24);
            tv.setBackgroundResource(R.drawable.bg_card_booking);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 12, 0, 12);
            tv.setLayoutParams(params);

            layoutBookingList.addView(tv);
            tv.setAlpha(0f);
            tv.animate().alpha(1f).setDuration(300).start();
        }
    }
}
