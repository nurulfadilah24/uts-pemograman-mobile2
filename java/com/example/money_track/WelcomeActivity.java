package com.example.money_track; // SESUAIKAN dengan package kamu

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView title, subtitle;
    ImageView flag;   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome); // pastikan nama XML benar

        // Hubungkan dengan XML
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        flag = findViewById(R.id.flag);

        // (Opsional) Kalau mau ubah teks lewat Java
        title.setText("Money Track");
        subtitle.setText("Selamat Datang");
    }
}
