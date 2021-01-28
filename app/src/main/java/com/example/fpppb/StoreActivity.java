package com.example.fpppb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class StoreActivity extends AppCompatActivity {
    private static final int kodekamera = 222;
    private File file;
    private ImageButton upload;
    private Button submit;
    private Spinner kota, provinsi, lokasi;
    private ApiInterface mApiInterface;
    private EditText judul, isi;
    private LocationManager lm;
    private LocationListener ll;
    private String cityName, stateName;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        lm = (LocationManager) getSystemService
                (Context.LOCATION_SERVICE);
        ll = new lokasiListener();

        upload=findViewById(R.id.uplaod);
        submit=findViewById(R.id.submit);
        judul=findViewById(R.id.store_judul);
        isi=findViewById(R.id.store_isi);
        lokasi=findViewById(R.id.switch_lokasi);
        kota=findViewById(R.id.store_kota);
        provinsi=findViewById(R.id.store_provinsi);
        bar=findViewById(R.id.progressBar2);


        upload.setOnClickListener(operasi);
        submit.setOnClickListener(operasi);

        String[] lokasi_list=new String[]{"Lokasi Manual", "Lokasi Otomatis"};

        ArrayAdapter<String> adapter_lokasi = get_spinner_adapter(lokasi_list);
        lokasi.setAdapter(adapter_lokasi);

        getProvinsi();

        lokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ganti_lokasi_otomatis(lokasi.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(lokasi.getSelectedItem().toString().equals("Lokasi Manual")){
                    gantiKota(provinsi.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public ArrayAdapter<String> get_spinner_adapter(String[] list){
        return new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;

            }
        };
    }

    private void getProvinsi(){
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

                ArrayAdapter<String> adapter_provinsi = get_spinner_adapter(provinsi_list);
                provinsi.setAdapter(adapter_provinsi);
                ArrayAdapter<String> adapter_kota = get_spinner_adapter(kota_list);
                kota.setAdapter(adapter_kota);
            }

            @Override
            public void onFailure(Call<GetProvince> call, Throwable t) {
                Log.e("Retrofit Get", "dataaaaaaaaaaa: " +
                        String.valueOf(t));
            }
        });
    }
    View.OnClickListener operasi=new View.OnClickListener(){

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.uplaod:upload();break;
                case R.id.submit:submit();break;
            }
        }
    };

    private void upload(){
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, kodekamera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode) {
                case (kodekamera):prosesKamera(data);
                break;
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
            Log.e("loc Get", "ada: " +
                    String.valueOf("lalala"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loc Get", "ada: " +
                    String.valueOf((e)));
            return null;
        }

        return file;
    }




    private void gantiKota(String provinsis){
        Call<DetailRegencies> get=mApiInterface.detailRegencies(provinsis);
        get.enqueue(new Callback<DetailRegencies>(){

            @Override
            public void onResponse(Call<DetailRegencies> call, Response<DetailRegencies> response) {
                List<String> where = new ArrayList<String>();

                assert response.body() != null;
                    for (Regencies province : response.body().getData()) {
                        where.add(province.getName());
                    }
                    String[] kota_ganti = new String[where.size()];
                    where.toArray(kota_ganti);
                    ArrayAdapter<String> adapter_kota = get_spinner_adapter(kota_ganti);
                kota.setAdapter(adapter_kota);
            }


            @Override
            public void onFailure(Call<DetailRegencies> call, Throwable t) {

            }
        });
    }

    private void ganti_lokasi_otomatis(String pesan){
        if(pesan.equals("Lokasi Otomatis")){
            bar.setVisibility(View.VISIBLE);
            kota.setEnabled(false);
            provinsi.setEnabled(false);
            submit.setEnabled(false);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            lm.requestLocationUpdates
                    (LocationManager.GPS_PROVIDER, 0, 0, ll);

        }
        else{
            lm.removeUpdates(ll);
            bar.setVisibility(View.GONE);
            kota.setEnabled(true);
            provinsi.setEnabled(true);
            submit.setEnabled(true);
            getProvinsi();
        }
    }
    private class lokasiListener implements LocationListener {
        private TextView txtLat, txtLong;

        @Override
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(StoreActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                cityName = addresses.get(0).getAddressLine(0);
                String[] arrOfStr = cityName.split(",", 0);

                String[] kota_alamat=new String[]{arrOfStr[4]};
                String[] provinsi_alamat=new String[]{arrOfStr[5].substring(0, arrOfStr[5].length()-5)};

                Toast.makeText(StoreActivity.this, Arrays.toString(provinsi_alamat), Toast.LENGTH_LONG);
                Log.e("loc Get", "ada: " +
                        String.valueOf(Arrays.toString(provinsi_alamat) +"lalala"));
                ArrayAdapter<String> adapter_provinsi = get_spinner_adapter(provinsi_alamat);;
                provinsi.setAdapter(adapter_provinsi);
                ArrayAdapter<String> adapter_kota = get_spinner_adapter(kota_alamat);
                kota.setAdapter(adapter_kota);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("loc Get", "error: " +
                        String.valueOf(e));
            }

            bar.setVisibility(View.GONE);
            kota.setEnabled(true);
            provinsi.setEnabled(true);
            submit.setEnabled(true);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    private void submit(){
        if(file==null){
            AlertDialog.Builder adb=new AlertDialog.Builder(StoreActivity.this);
            adb.setTitle("Error");
            adb.setMessage("Ambil Foto !");
            adb.setPositiveButton("Ok", null);
            adb.show();
            return;
        }
        if(judul.getText().toString().equals("")){
            judul.setError("Judul Harus Diisi !");
            return;
        }
        if(isi.getText().toString().equals("")){
            isi.setError("Isi Harus Diisi !");
            return;
        }
        if(provinsi.getSelectedItem().toString().equals("Pilih Provinsi")){
            AlertDialog.Builder adb=new AlertDialog.Builder(StoreActivity.this);
            adb.setTitle("Error");
            adb.setMessage("Pilih Provinsi ! ");
            adb.setPositiveButton("Ok", null);
            adb.show();
            return;
        }
        if(kota.getSelectedItem().toString().equals("Pilih Provinsi Dulu")){
            AlertDialog.Builder adb=new AlertDialog.Builder(StoreActivity.this);
            adb.setTitle("Error");
            adb.setMessage("Pilih Kota ! ");
            adb.setPositiveButton("Ok", null);
            adb.show();
            return;
        }

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", "temp.png", RequestBody.create(MediaType.parse("image/*"), file));

        RequestBody judull = RequestBody.create(MediaType.parse("text/plain"), judul.getText().toString());
        RequestBody isii = RequestBody.create(MediaType.parse("text/plain"), isi.getText().toString());
        RequestBody kotaa = RequestBody.create(MediaType.parse("text/plain"), kota.getSelectedItem().toString());
        RequestBody provinsii = RequestBody.create(MediaType.parse("text/plain"), provinsi.getSelectedItem().toString());

        Call<PostBacot> post=mApiInterface.postBacot(filePart, judull, isii , kotaa, provinsii);
        post.enqueue(new Callback<PostBacot>() {
            @Override
            public void onResponse(Call<PostBacot> call, Response<PostBacot> response) {
                Log.d("Retrofit Get", "data Kontak: " +
                        String.valueOf(response));
                Toast.makeText(StoreActivity.this, "Data Tersimpan", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<PostBacot> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(StoreActivity.this, "Data Gagal Disimpan", Toast.LENGTH_LONG).show();
            }
        });


    }

}