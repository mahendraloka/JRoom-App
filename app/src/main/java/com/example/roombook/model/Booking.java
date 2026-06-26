package com.example.roombook.model;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("id")
    private int id;

    @SerializedName("ruangan_id")
    private int ruanganId;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("waktu_mulai")
    private String waktuMulai;

    @SerializedName("waktu_selesai")
    private String waktuSelesai;

    @SerializedName("pemesan")
    private String pemesan;

    // Getter
    public int getId() {
        return id;
    }

    public int getRuanganId() {
        return ruanganId;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public String getPemesan() {
        return pemesan;
    }

    @SerializedName("tujuan")
    private String tujuan;

    public String getTujuan() {
        return tujuan;
    }

    @SerializedName("nama")
    private String nama;

    public String getNama() {
        return nama;
    }


}
