package com.example.money_track;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Terapkan bahasa dari BaseActivity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi View
        ImageView ivBendera = findViewById(R.id.ic_bendera);
        Button btnMulai = findViewById(R.id.btn_mulai);

        // 2. Pembuatan Notification Channel (PENTING untuk Firebase)
        // Tanpa ini, notifikasi dari database/console tidak akan muncul di layar
        buatNotificationChannel();

        // 3. Update Bendera Otomatis
        updateBendera(ivBendera);

        // 4. Logika Tombol Mulai
        btnMulai.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TambahTransaksiActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Membuat saluran notifikasi agar pesan dari Firebase Console
     * dapat tampil di perangkat Android 8.0 (API 26) ke atas.
     */
    private void buatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel ID harus unik dan konsisten dengan MyFirebaseMessagingService
            String channelId = "MONEY_TRACK_NOTIF";
            CharSequence name = "Pengingat Transaksi";
            String description = "Saluran untuk notifikasi rekap harian";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView ivBendera = findViewById(R.id.ic_bendera);
        updateBendera(ivBendera);
    }
}