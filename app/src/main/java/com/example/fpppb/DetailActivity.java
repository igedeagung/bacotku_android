package com.example.fpppb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fpppb.Model.Bacot;
import com.example.fpppb.Model.DetailBacot;
import com.example.fpppb.Model.GetBacot;
import com.example.fpppb.Rest.ApiClient;
import com.example.fpppb.Rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        int id=extras.getInt("id");

        ImageView gambar=findViewById(R.id.gambar_detail);
        TextView judul=findViewById(R.id.judul_detail);
        TextView isi=findViewById(R.id.isi_detail);
        Button lokasi=findViewById(R.id.lokasi);

        ApiInterface mApiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<DetailBacot> get=mApiInterface.detailBacot(id);
        get.enqueue(new Callback<DetailBacot>(){

            @Override
            public void onResponse(Call<DetailBacot> call, Response<DetailBacot> response) {
                Bacot isibacot=response.body().getData();
                judul.setText(isibacot.getJudul());
                isi.setText(isibacot.getIsi());
                lokasi.setText(isibacot.getKota()+", "+isibacot.getProvinsi());

                byte[] imagee= Base64.decode(isibacot.getImage(), Base64.DEFAULT);;

                Bitmap bitmap = BitmapFactory.decodeByteArray(imagee, 0, imagee.length);
                gambar.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Call<DetailBacot> call, Throwable t) {
                Log.e("Retrofit Get", "dataaaaaaaaaaa: " +
                        String.valueOf(t));
            }
        });

        lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent maps=new Intent(DetailActivity.this, MapsActivity.class);
                maps.putExtra("kota", lokasi.getText().toString());
                startActivity(maps);
            }
        });
    }
}