package com.example.fpppb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fpppb.Model.Bacot;
import com.example.fpppb.Model.DetailBacot;
import com.example.fpppb.Model.DetailRegencies;
import com.example.fpppb.Model.GetProvince;
import com.example.fpppb.Model.PostBacot;
import com.example.fpppb.Model.Province;
import com.example.fpppb.Model.Regencies;
import com.example.fpppb.Rest.ApiClient;
import com.example.fpppb.Rest.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreActivity extends AppCompatActivity {
    private static final int kodekamera = 222;
    private File file;
    private ImageButton upload;
    private Button tanggal;
    private Calendar myCalendar;
    private Spinner kota, provinsi;
    private ApiInterface mApiInterface;
    private EditText judul, isi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        upload=findViewById(R.id.uplaod);
        tanggal=findViewById(R.id.tanggal);
        Button submit=findViewById(R.id.submit);
        judul=findViewById(R.id.store_judul);
        isi=findViewById(R.id.store_isi);
        Switch lokasi=findViewById(R.id.switch_lokasi);
        kota=findViewById(R.id.store_kota);
        provinsi=findViewById(R.id.store_provinsi);
        myCalendar = Calendar.getInstance();

        upload.setOnClickListener(operasi);
        tanggal.setOnClickListener(operasi);
        submit.setOnClickListener(operasi);

        mApiInterface= ApiClient.getClient().create(ApiInterface.class);
        Call<GetProvince> get=mApiInterface.getProvince();
        get.enqueue(new Callback<GetProvince>(){
            @Override
            public void onResponse(Call<GetProvince> call, Response<GetProvince> response) {
                List<String> where = new ArrayList<String>();

                for (Province province : response.body().getData()) {
                    where.add(province.getName());
                }
                String[] provinsi_list = new String[ where.size() ];
                where.toArray( provinsi_list );
                String[] kota_list=new String[]{"Pilih Provinsi Dulu"};

                ArrayAdapter<String> adapter_provinsi = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, provinsi_list);
                provinsi.setAdapter(adapter_provinsi);
                ArrayAdapter<String> adapter_kota = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, kota_list);
                kota.setAdapter(adapter_kota);
            }

            @Override
            public void onFailure(Call<GetProvince> call, Throwable t) {
                Log.e("Retrofit Get", "dataaaaaaaaaaa: " +
                        String.valueOf(t));
            }
        });

        provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gantiKota(provinsi.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    View.OnClickListener operasi=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.uplaod:upload();break;
                case R.id.tanggal:pilih_tanggal();break;
                case R.id.submit:submit();break;
            }
        }
    };

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void upload(){
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, kodekamera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {   super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {   switch (requestCode) {
            case (kodekamera):prosesKamera(data);break;
        }
        }
    }

    private void prosesKamera(Intent datanya)
    {
        Bitmap bm = (Bitmap) datanya.getExtras().get("data");
        file = savebitmap(bm);

        upload.setImageBitmap(bm); // Set imageview to image that was
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    private void pilih_tanggal(){
        new DatePickerDialog(StoreActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggal.setText(sdf.format(myCalendar.getTime()));
    }

    private void gantiKota(String provinsis){
        Call<DetailRegencies> get=mApiInterface.detailRegencies(provinsis);
        get.enqueue(new Callback<DetailRegencies>(){

            @Override
            public void onResponse(Call<DetailRegencies> call, Response<DetailRegencies> response) {
                List<String> where = new ArrayList<String>();

                for (Regencies province : response.body().getData()) {
                    where.add(province.getName());
                }
                String[] kota_ganti = new String[ where.size() ];
                where.toArray( kota_ganti );
                ArrayAdapter<String> adapter_kota = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, kota_ganti);
                kota.setAdapter(adapter_kota);
            }

            @Override
            public void onFailure(Call<DetailRegencies> call, Throwable t) {

            }
        });
    }

    private void submit(){
        validate_photo();
        validate_judul();
        validate_isi();
        validate_provinsi();
        validate_kota();

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", "temp.png", RequestBody.create(MediaType.parse("image/*"), file));

        RequestBody tanggall = RequestBody.create(MediaType.parse("text/plain"), tanggal.getText().toString());
        RequestBody judull = RequestBody.create(MediaType.parse("text/plain"), judul.getText().toString());
        RequestBody isii = RequestBody.create(MediaType.parse("text/plain"), isi.getText().toString());
        RequestBody kotaa = RequestBody.create(MediaType.parse("text/plain"), kota.getSelectedItem().toString());
        RequestBody provinsii = RequestBody.create(MediaType.parse("text/plain"), provinsi.getSelectedItem().toString());

        Call<PostBacot> post=mApiInterface.postBacot(filePart, tanggall, judull,isii , kotaa, provinsii);
        post.enqueue(new Callback<PostBacot>() {
            @Override
            public void onResponse(Call<PostBacot> call, Response<PostBacot> response) {
                Log.d("Retrofit Get", "data Kontak: " +
                        String.valueOf(response));
                Toast.makeText(StoreActivity.this, "Data Tersimpan", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PostBacot> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(StoreActivity.this, "Data Gagal Disimpan", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void validate_kota() {

    }

    private void validate_provinsi() {

    }

    private void validate_isi() {
    }

    private void validate_judul() {
    }

    private void validate_photo(){

    }
}