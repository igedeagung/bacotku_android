package com.example.fpppb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.example.fpppb.Model.Bacot;
import com.example.fpppb.Model.GetBacot;
import com.example.fpppb.Rest.ApiClient;
import com.example.fpppb.Rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText edittext;
    private BacotAdapter bAdapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add;

        lv=findViewById(R.id.lv);
        add=findViewById(R.id.add);

        ArrayList<Bacot> listKontak = new ArrayList<Bacot>();
        bAdapter = new BacotAdapter(this,0,listKontak);
        lv.setAdapter(bAdapter);

        ambildata();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent store=new Intent(MainActivity.this, StoreActivity.class);
                startActivity(store);
            }
        });
    }

    private void ambildata(){
        ApiInterface mApiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<GetBacot> get=mApiInterface.getBacot();
        get.enqueue(new Callback<GetBacot>() {

            @Override
            public void onResponse(Call<GetBacot> call, Response<GetBacot> response) {
                Log.d("Retrofit Get", "dataaaaaaaaaaa: " +
                        String.valueOf(response.body().getData()));

                for (Bacot bacot : response.body().getData()) {
                    if(bacot.getIsi().length()>50) {
                        bacot.setIsi(bacot.getIsi().substring(0, 49) + " ...");
                    }
                    bAdapter.add(bacot);
                }
            }

            @Override
            public void onFailure(Call<GetBacot> call, Throwable t) {
                Log.e("Retrofit Get", "dataaaaaaaaaaa: " +
                        String.valueOf(t));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bAdapter.clear();
        ambildata();
    }
}