package com.example.money_track;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private LinearLayout containerRect, layoutContent;
    private View rectLeft, rectRight;
    private ImageView ivSplashFlag;
    private TextView tvSearchingLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inisialisasi View
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        containerRect = findViewById(R.id.container_rect);
        layoutContent = findViewById(R.id.layout_content);
        rectLeft = findViewById(R.id.rect_left);
        rectRight = findViewById(R.id.rect_right);
        ivSplashFlag = findViewById(R.id.iv_splash_flag);
        tvSearchingLocation = findViewById(R.id.tv_searching_location);

        tvSearchingLocation.setText(getString(R.string.searching_location));

        // 1. Cek Semua Izin (Lokasi & Notifikasi)
        periksaDanMintaIzin();
    }

    private void periksaDanMintaIzin() {
        List<String> listPermissionsNeeded = new ArrayList<>();

        // Izin Lokasi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Izin Notifikasi (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            mulaiProsesAplikasi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Tetap jalankan aplikasi meskipun izin notifikasi ditolak, selama lokasi dicek
            mulaiProsesAplikasi();
        }
    }

    private void mulaiProsesAplikasi() {
        // Ambil koordinat GPS
        ambilKoordinat();

        // Jalankan Animasi Rotasi
        Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        containerRect.startAnimation(rotateAnim);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    tvSearchingLocation.animate().alpha(0f).setDuration(400).withEndAction(() -> {
                        jalankanAnimasiBukaPintu();
                    }).start();
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void ambilKoordinat() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvSearchingLocation.setText(getString(R.string.gps_off));
            saveLocationConfig("ID");
            return;
        }

        CancellationTokenSource cts = new CancellationTokenSource();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            prosesGeocoder(location.getLatitude(), location.getLongitude());
                        } else {
                            tvSearchingLocation.setText(getString(R.string.failed_location));
                        }
                    });
        }
    }

    private void prosesGeocoder(double lat, double lon) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(SplashActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String code = addresses.get(0).getCountryCode();
                    saveLocationConfig(code);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void saveLocationConfig(String code) {
        SharedPreferences pref = getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("COUNTRY_CODE", code);

        String lang = (code != null && code.equalsIgnoreCase("ID")) ? "in" : "en";
        editor.putString("LANG", lang);
        editor.apply();

        runOnUiThread(() -> {
            BaseActivity.terapkanBahasa(SplashActivity.this);
            String message = getString(R.string.location_detected, code);
            tvSearchingLocation.setText(message);
        });
    }

    private void jalankanAnimasiBukaPintu() {
        rectLeft.animate().translationX(-2000f).setDuration(1200).start();
        rectRight.animate().translationX(2000f).setDuration(1200).start();

        layoutContent.animate().alpha(1f).setDuration(800).withEndAction(() -> {
            updateBendera(ivSplashFlag);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 1500);
        }).start();
    }
}