package com.example.fpppb.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Bacot {
    private int id;
    private String namafile;
    private String tanggal;
    private String judul;
    private String isi;
    private String kota;
    private String provinsi;
    private String created_at_date;
    private String updated_at_date;
    private String image;

    public String getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getNamafile() {
        return namafile;
    }

    public String getJudul() {
        return judul;
    }

    public String getIsi() {
        return isi;
    }

    public String getKota() {
        return kota;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public String getCreated_at_date() {
        return created_at_date;
    }

    public String getUpdated_at_date() {
        return updated_at_date;
    }
}

