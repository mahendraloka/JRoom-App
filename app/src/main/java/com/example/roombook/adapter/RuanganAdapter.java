package com.example.roombook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roombook.R;
import com.example.roombook.model.Ruangan;

import java.util.List;

public class RuanganAdapter extends RecyclerView.Adapter<RuanganAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Ruangan ruangan);
    }

    private final List<Ruangan> ruanganList;
    private final OnItemClickListener listener;

    public RuanganAdapter(List<Ruangan> ruanganList, OnItemClickListener listener) {
        this.ruanganList = ruanganList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ruangan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ruangan ruangan = ruanganList.get(position);
        holder.bind(ruangan, listener);
    }

    @Override
    public int getItemCount() {
        return ruanganList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNamaRuangan, tvLantai, tvKapasitas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaRuangan = itemView.findViewById(R.id.tv_nama_ruangan);
            tvLantai = itemView.findViewById(R.id.tv_lantai);
            tvKapasitas = itemView.findViewById(R.id.tv_kapasitas);
        }

        public void bind(final Ruangan ruangan, final OnItemClickListener listener) {
            tvNamaRuangan.setText(ruangan.getNama());
            tvLantai.setText("Lantai: " + ruangan.getLantai());
            tvKapasitas.setText("Kapasitas: " + ruangan.getKapasitas() + " orang");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(ruangan);
                }
            });
        }
    }
}
