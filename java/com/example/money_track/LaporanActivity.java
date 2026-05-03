package com.example.money_track;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

public class LaporanActivity extends BaseActivity { // Gunakan BaseActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        // Inisialisasi View
        TextView tvTotalPemasukan = findViewById(R.id.tv_total_pemasukan);
        TextView tvTotalPengeluaran = findViewById(R.id.tv_total_pengeluaran);
        TextView tvSisaSaldo = findViewById(R.id.tv_sisa_saldo);
        Button btnKembali = findViewById(R.id.btn_kembali_beranda);
        View barPemasukan = findViewById(R.id.bar_pemasukan);
        View barPengeluaran = findViewById(R.id.bar_pengeluaran);

        // 1. Ambil Data Negara & Kurs
        SharedPreferences configPref = getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
        String countryCode = configPref.getString("COUNTRY_CODE", "ID");

        // 2. Ambil Data Transaksi (Basis Rupiah)
        SharedPreferences sharedPref = getSharedPreferences("DataTransaksi", Context.MODE_PRIVATE);
        long masukRupiah = sharedPref.getLong("TOTAL_MASUK", 0);
        long keluarRupiah = sharedPref.getLong("TOTAL_KELUAR", 0);
        long sisaRupiah = masukRupiah - keluarRupiah;

        // 3. Logika Format Mata Uang & Konversi
        String simbol;
        NumberFormat formatter;
        double factor = 1.0;

        if (countryCode != null && countryCode.equalsIgnoreCase("SG")) {
            simbol = "SGD";
            formatter = NumberFormat.getInstance(Locale.US);
            factor = 11500.0; // Kurs konversi
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
        } else {
            simbol = "Rp";
            formatter = NumberFormat.getInstance(new Locale("in", "ID"));
            formatter.setMaximumFractionDigits(0);
        }

        // 4. Terapkan Teks yang Sudah Dikonversi
        tvTotalPemasukan.setText(getString(R.string.currency_format, simbol, formatter.format(masukRupiah / factor)));
        tvTotalPengeluaran.setText(getString(R.string.currency_format, simbol, formatter.format(keluarRupiah / factor)));
        tvSisaSaldo.setText(getString(R.string.currency_format, simbol, formatter.format(sisaRupiah / factor)));

        // Update visual grafik
        updateGrafik(barPemasukan, barPengeluaran, masukRupiah, keluarRupiah);

        btnKembali.setOnClickListener(v -> {
            Intent intent = new Intent(LaporanActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void updateGrafik(View barMasuk, View barKeluar, long masuk, long keluar) {
        if (masuk == 0 && keluar == 0) return;
        int maxHeight = 300;

        if (masuk >= keluar && masuk > 0) {
            barMasuk.getLayoutParams().height = maxHeight;
            barKeluar.getLayoutParams().height = (int) (maxHeight * ((float) keluar / masuk));
        } else if (keluar > masuk) {
            barKeluar.getLayoutParams().height = maxHeight;
            barMasuk.getLayoutParams().height = (int) (maxHeight * ((float) masuk / keluar));
        }
        barMasuk.requestLayout();
        barKeluar.requestLayout();
    }
}