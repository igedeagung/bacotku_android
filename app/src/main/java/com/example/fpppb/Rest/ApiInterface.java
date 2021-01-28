package com.example.fpppb.Rest;

import com.example.fpppb.Model.Bacot;
import com.example.fpppb.Model.DetailBacot;
import com.example.fpppb.Model.DetailRegencies;
import com.example.fpppb.Model.GetBacot;
import com.example.fpppb.Model.GetProvince;
import com.example.fpppb.Model.PostBacot;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("show")
    Call<GetBacot> getBacot();

    @Multipart
    @POST("store")
    Call<PostBacot> postBacot(@Part MultipartBody.Part photo, @Part("judul") RequestBody judul, @Part("isi") RequestBody isi, @Part("kota") RequestBody kota,@Part("provinsi") RequestBody provinsi );

    @GET("detail/{id}")
    Call<DetailBacot> detailBacot(@Path(value = "id") int id);

    @GET("province")
    Call<GetProvince> getProvince();

    @GET("regencies/{id}")
    Call<DetailRegencies> detailRegencies(@Path(value = "id") String id);
}
