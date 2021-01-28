package com.example.fpppb;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.fpppb.Model.Bacot;

import java.util.List;

public class BacotAdapter extends ArrayAdapter<Bacot> {
    private List<Bacot> list;
    public BacotAdapter(Context context, int resource, List<Bacot> objects) {
        super(context, resource, objects);
        this.list=objects;
    }
    public View getView(final int position, View ConvertView, ViewGroup parent) {
        Bacot dtbacot = getItem(position);
        if (ConvertView == null) {
            ConvertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bacot, parent, false);
        }
        final TextView tjudul = (TextView) ConvertView.findViewById(R.id.judul);
        final TextView tisi = (TextView) ConvertView.findViewById(R.id.isi);
        final ImageView gambar = (ImageView) ConvertView.findViewById(R.id.imageView);

        Button detail = ConvertView.findViewById(R.id.detail);

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detail=new Intent(getContext(), DetailActivity.class);
                detail.putExtra("id", dtbacot.getId());
                getContext().startActivity(detail);
            }
        });

        tjudul.setText(dtbacot.getJudul());
        tisi.setText(dtbacot.getIsi());

        byte[] imagee = Base64.decode(dtbacot.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagee, 0, imagee.length);
        gambar.setImageBitmap(bitmap);

        return ConvertView;
    }
}
