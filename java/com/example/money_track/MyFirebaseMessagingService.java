package com.example.money_track;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Menangani pesan notifikasi dari Firebase Console/Database
        if (remoteMessage.getNotification() != null) {
            tampilkanNotifikasi(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }
    }

    private void tampilkanNotifikasi(String title, String messageBody) {
        String channelId = "MONEY_TRACK_NOTIF";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 1. Buat Notification Channel jika menjalankan Android Oreo (API 26) atau lebih baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Pengingat Transaksi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Saluran untuk notifikasi rekap harian");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 2. Bangun Notifikasi
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo_moneytrack) // Pastikan file logo_moneytrack tersedia di folder res/drawable
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 3. Tampilkan Notifikasi
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}