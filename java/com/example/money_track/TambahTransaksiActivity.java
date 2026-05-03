package com.example.money_track;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;

public class TambahTransaksiActivity extends BaseActivity {

    private TextView tvUsername, tvInfoSaldo;
    private LinearLayout btnTambahTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_transaksi);

        tvUsername = findViewById(R.id.tv_username);
        tvInfoSaldo = findViewById(R.id.tv_info_saldo);
        btnTambahTransaksi = findViewById(R.id.btn_tambah_transaksi);

        tvUsername.setText("Budi Santoso");

        tampilkanSaldoTerakhir();

        btnTambahTransaksi.setOnClickListener(v -> {
            Intent intent = new Intent(TambahTransaksiActivity.this, InputTransaksiActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tampilkanSaldoTerakhir();
    }

    private void tampilkanSaldoTerakhir() {
        // 1. Ambil data negara yang dideteksi GPS (dari SplashActivity)
        SharedPreferences configPref = getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
        String countryCode = configPref.getString("COUNTRY_CODE", "ID");

        // 2. Ambil data saldo mentah dalam Rupiah (sebagai basis data utama)
        SharedPreferences sharedPref = getSharedPreferences("DataTransaksi", Context.MODE_PRIVATE);
        long masuk = sharedPref.getLong("TOTAL_MASUK", 100000); // Contoh saldo 100rb
        long keluar = sharedPref.getLong("TOTAL_KELUAR", 0);
        double sisaDasar = (double) (masuk - keluar);

        String simbolMataUang;
        NumberFormat formatter;
        double saldoDikonversi;

        // 3. Logika Konversi Mata Uang & Format Angka
        if (countryCode != null && countryCode.equalsIgnoreCase("SG")) {
            // JIKA DI SINGAPURA
            simbolMataUang = "SGD ";
            formatter = NumberFormat.getInstance(Locale.US); // Format internasional (1,000.00)

            // KURS KONVERSI (Contoh: 1 SGD = Rp 11.500)
            double kursSgd = 11500.0;
            saldoDikonversi = sisaDasar / kursSgd;

            // Atur agar Dollar menampilkan 2 angka di belakang koma (sen)
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
        } else {
            // JIKA DI INDONESIA
            simbolMataUang = "Rp ";
            formatter = NumberFormat.getInstance(new Locale("in", "ID")); // Format lokal (1.000)
            saldoDikonversi = sisaDasar;

            // Rupiah biasanya tidak menggunakan desimal
            formatter.setMaximumFractionDigits(0);
        }

        // 4. Gabungkan Simbol dan Angka hasil konversi
        String saldoFinal = simbolMataUang + formatter.format(saldoDikonversi);

        // 5. Tampilkan menggunakan String Resource (Multibahasa otomatis)
        tvInfoSaldo.setText(getString(R.string.balance_info, saldoFinal));
    }
}