package com.example.money_track;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InputTransaksiActivity extends BaseActivity { // 1. Gunakan BaseActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Terapkan bahasa sebelum memuat layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_transaksi);

        Spinner spinnerJenis = findViewById(R.id.spinner_jenis);
        EditText etPemasukan = findViewById(R.id.et_pemasukan);
        EditText etPengeluaran = findViewById(R.id.et_pengeluaran);
        Button btnSimpan = findViewById(R.id.btn_simpan);

        // 2. Gunakan array dari strings.xml agar pilihan Spinner otomatis berubah bahasa
        // (Pastikan android:entries="@array/transaction_types" sudah ada di XML)

        btnSimpan.setOnClickListener(v -> {
            String masukStr = etPemasukan.getText().toString().trim();
            String keluarStr = etPengeluaran.getText().toString().trim();

            if (masukStr.isEmpty() && keluarStr.isEmpty()) {
                Toast.makeText(this, "Mohon isi nominal", Toast.LENGTH_SHORT).show();
                return;
            }

            long masukBaru = masukStr.isEmpty() ? 0 : Long.parseLong(masukStr);
            long keluarBaru = keluarStr.isEmpty() ? 0 : Long.parseLong(keluarStr);

            // 3. Logika Penyimpanan Data
            // Kita menyimpan nilai asli yang diinput user.
            // Jika user di Singapura, asumsikan mereka menginput dalam nilai SGD.
            // Kita perlu menyamakan basis data (misal ke Rupiah) agar perhitungan saldo sinkron.

            SharedPreferences configPref = getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
            String countryCode = configPref.getString("COUNTRY_CODE", "ID");

            double kursSgd = 11500.0; // Kurs standar
            long masukSimpan, keluarSimpan;

            if (countryCode != null && countryCode.equalsIgnoreCase("SG")) {
                // Konversi input SGD user ke Rupiah sebelum disimpan ke database
                masukSimpan = (long) (masukBaru * kursSgd);
                keluarSimpan = (long) (keluarBaru * kursSgd);
            } else {
                masukSimpan = masukBaru;
                keluarSimpan = keluarBaru;
            }

            // SIMPAN DATA KE SHAREDPREFERENCES
            SharedPreferences sharedPref = getSharedPreferences("DataTransaksi", Context.MODE_PRIVATE);
            long totalMasukLama = sharedPref.getLong("TOTAL_MASUK", 0);
            long totalKeluarLama = sharedPref.getLong("TOTAL_KELUAR", 0);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("TOTAL_MASUK", totalMasukLama + masukSimpan);
            editor.putLong("TOTAL_KELUAR", totalKeluarLama + keluarSimpan);
            editor.apply();

            // Pindah ke Laporan
            Intent intent = new Intent(InputTransaksiActivity.this, LaporanActivity.class);
            startActivity(intent);
            finish();
        });
    }
}