package com.example.roombook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombook.api.ApiClient;
import com.example.roombook.api.ApiService;
import com.example.roombook.model.Ruangan;
import com.example.roombook.adapter.RuanganAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckAvailabilityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RuanganAdapter adapter;
    private List<Ruangan> ruanganList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_availability);

        recyclerView = findViewById(R.id.rvRuangan);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RuanganAdapter(ruanganList, ruangan -> {
            Intent intent = new Intent(CheckAvailabilityActivity.this, RoomStatusActivity.class);
            intent.putExtra("ruangan_id", ruangan.getId());
            intent.putExtra("ruangan_nama", ruangan.getNama());
            intent.putExtra("ruangan_lantai", ruangan.getLantai());
            intent.putExtra("ruangan_kapasitas", ruangan.getKapasitas());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadRuangan();
    }


    private void loadRuangan() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        ApiService api = ApiClient.getApiService();
        api.getDaftarRuangan().enqueue(new Callback<List<Ruangan>>() {
            @Override
            public void onResponse(Call<List<Ruangan>> call, Response<List<Ruangan>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    ruanganList.clear();
                    ruanganList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CheckAvailabilityActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ruangan>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CheckAvailabilityActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
