package com.example.roombook;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roombook.api.ApiClient;
import com.example.roombook.api.ApiService;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etNim, etProdi, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNama = findViewById(R.id.etNama);
        etNim = findViewById(R.id.etNim);
        etProdi = findViewById(R.id.etProdi);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String nim = etNim.getText().toString().trim();
            String prodi = etProdi.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nama.isEmpty() || nim.isEmpty() || prodi.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("ums.ac.id")) {
                Toast.makeText(this, "Gunakan email UMS", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(nama, nim, prodi, email, password);
        });
    }

    private void registerUser(String nama, String nim, String prodi, String email, String password) {
        ApiService api = ApiClient.getApiService();
        Map<String, String> data = new HashMap<>();
        data.put("action", "register");
        data.put("nama", nama);
        data.put("nim", nim);
        data.put("prodi", prodi);
        data.put("email", email);
        data.put("password", password);

        api.registerUser(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String status = response.body().get("status").getAsString();
                    if (status.equals("success")) {
                        Toast.makeText(RegisterActivity.this, "Registrasi berhasil. Silakan login.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = response.body().has("message") ?
                                response.body().get("message").getAsString() : "Registrasi gagal";
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Registrasi gagal (server error)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
