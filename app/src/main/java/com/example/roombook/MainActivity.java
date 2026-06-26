package com.example.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnCheckAvailability, btnBooking, btnKalenderAkademik, btnRiwayat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnBooking = findViewById(R.id.btnBooking);
        btnKalenderAkademik = findViewById(R.id.btnKalenderAkademik); // tombol Kalender Akademik
        btnRiwayat = findViewById(R.id.btnRiwayat); // tombol Riwayat Peminjaman

        btnCheckAvailability.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CheckAvailabilityActivity.class));
        });

        btnBooking.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookingActivity.class));
        });

        btnKalenderAkademik.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", "https://news.ums.ac.id/id/07/2024/download-kalender-akademik-ums-tahun-2024-2025/");
            startActivity(intent);
        });

        btnRiwayat.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RiwayatBookingActivity.class));
        });
    }
}
