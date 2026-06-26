package com.example.roombook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roombook.api.ApiClient;
import com.example.roombook.api.ApiService;
import com.example.roombook.model.Ruangan;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.JsonObject;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    private EditText edtNama, edtTujuan;
    private Button btnTanggal, btnWaktuMulai, btnWaktuSelesai;
    private Spinner spinnerRuangan;
    private Button btnSubmit;
    private List<Ruangan> ruanganList;
    private int selectedRuanganId;

    private final Set<String> tanggalLibur = new HashSet<>();
    private String selectedTanggal = null;
    private String emailPemesan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        edtNama = findViewById(R.id.etNamaPemesan);
        edtTujuan = findViewById(R.id.etTujuan);
        btnTanggal = findViewById(R.id.btnPilihTanggal);
        btnWaktuMulai = findViewById(R.id.btnWaktuMulai);
        btnWaktuSelesai = findViewById(R.id.btnWaktuSelesai);
        spinnerRuangan = findViewById(R.id.spinnerRuangan);
        btnSubmit = findViewById(R.id.btnSubmit);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        emailPemesan = prefs.getString("email", null);

        ambilHariLibur();
        setupDatePicker();
        setupTimePicker(btnWaktuMulai, true);
        setupTimePicker(btnWaktuSelesai, false);
        loadRuanganData();

        btnSubmit.setOnClickListener(v -> submitBooking());
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
                Toast.makeText(BookingActivity.this, "Gagal mengambil data hari libur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        btnTanggal.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Pilih Tanggal Booking")
                    .build();

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar selected = Calendar.getInstance();
                selected.setTimeInMillis(selection);

                int dayOfWeek = selected.get(Calendar.DAY_OF_WEEK);
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                        selected.get(Calendar.YEAR),
                        selected.get(Calendar.MONTH) + 1,
                        selected.get(Calendar.DAY_OF_MONTH));

                if (dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(this, "⛔ Hari Minggu tidak dapat digunakan untuk booking", Toast.LENGTH_SHORT).show();
                    btnTanggal.setText("Pilih Tanggal");
                    selectedTanggal = null;
                } else if (tanggalLibur.contains(selectedDate)) {
                    Toast.makeText(this, "📅 Tanggal ini adalah hari libur nasional", Toast.LENGTH_SHORT).show();
                    btnTanggal.setText("Pilih Tanggal");
                    selectedTanggal = null;
                } else {
                    btnTanggal.setText("📅 " + selectedDate);
                    selectedTanggal = selectedDate;
                }
            });
        });
    }

    private void setupTimePicker(Button button, boolean isMulai) {
        button.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(9)
                    .setMinute(0)
                    .setTitleText(isMulai ? "Pilih Waktu Mulai" : "Pilih Waktu Selesai")
                    .build();

            picker.show(getSupportFragmentManager(), isMulai ? "PICKER_MULAI" : "PICKER_SELESAI");

            picker.addOnPositiveButtonClickListener(dialog -> {
                String waktu = String.format(Locale.getDefault(), "%02d:%02d", picker.getHour(), picker.getMinute());
                button.setText("⏰ " + waktu);
            });
        });
    }

    private void loadRuanganData() {
        ApiService api = ApiClient.getApiService();
        api.getDaftarRuangan().enqueue(new Callback<List<Ruangan>>() {
            @Override
            public void onResponse(Call<List<Ruangan>> call, Response<List<Ruangan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ruanganList = response.body();
                    setupRuanganSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<Ruangan>> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Gagal memuat data ruangan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRuanganSpinner() {
        List<String> ruanganNames = new ArrayList<>();
        for (Ruangan ruangan : ruanganList) {
            ruanganNames.add("🏢 " + ruangan.getNama() + " (Lantai " + ruangan.getLantai() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, ruanganNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRuangan.setAdapter(adapter);

        spinnerRuangan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRuanganId = ruanganList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String extractTime(String timeWithEmoji) {
        return timeWithEmoji.replace("⏰", "").trim();
    }

    private void submitBooking() {
        String nama = edtNama.getText().toString().trim();
        String tujuan = edtTujuan.getText().toString().trim();
        String waktuMulai = extractTime(btnWaktuMulai.getText().toString().trim());
        String waktuSelesai = extractTime(btnWaktuSelesai.getText().toString().trim());

        if (nama.isEmpty() || tujuan.isEmpty() || selectedTanggal == null || waktuMulai.isEmpty() || waktuSelesai.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi dengan benar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (waktuSelesai.compareTo(waktuMulai) <= 0) {
            Toast.makeText(this, "Waktu selesai harus lebih setelah waktu mulai", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailPemesan == null) {
            Toast.makeText(this, "Email tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getApiService();

        Map<String, Object> checkData = new HashMap<>();
        checkData.put("action", "cek_ketersediaan");
        checkData.put("ruangan_id", selectedRuanganId);
        checkData.put("tanggal", selectedTanggal);
        checkData.put("waktu_mulai", waktuMulai);
        checkData.put("waktu_selesai", waktuSelesai);

        api.cekKetersediaan(checkData).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean tersedia = response.body().get("tersedia").getAsBoolean();
                    if (!tersedia) {
                        Toast.makeText(BookingActivity.this, "❌ Ruangan sudah dibooking di hari tersebut", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> bookingData = new HashMap<>();
                    bookingData.put("action", "tambah_booking");
                    bookingData.put("ruangan_id", selectedRuanganId);
                    bookingData.put("tanggal", selectedTanggal);
                    bookingData.put("waktu_mulai", waktuMulai);
                    bookingData.put("waktu_selesai", waktuSelesai);
                    bookingData.put("pemesan", nama);
                    bookingData.put("tujuan", tujuan);
                    bookingData.put("email", emailPemesan);

                    api.tambahBooking(bookingData).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BookingActivity.this, "✅ Booking berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(BookingActivity.this, "❌ Gagal menyimpan booking", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(BookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(BookingActivity.this, "❌ Gagal mengecek ketersediaan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error saat cek ketersediaan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
