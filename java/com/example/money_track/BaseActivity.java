package com.example.money_track;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        terapkanBahasa(this);
        super.onCreate(savedInstanceState);
    }

    public static void terapkanBahasa(Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
            String lang = pref.getString("LANG", "en");

            Locale locale = new Locale(lang);
            Locale.setDefault(locale);

            Resources resources = context.getResources();
            Configuration config = new Configuration(resources.getConfiguration());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateBendera(ImageView ivBendera) {
        if (ivBendera == null) return;
        try {
            SharedPreferences pref = getSharedPreferences("ConfigApp", Context.MODE_PRIVATE);
            String countryCode = pref.getString("COUNTRY_CODE", "ID");

            if (countryCode != null && countryCode.equalsIgnoreCase("SG")) {
                ivBendera.setImageResource(R.drawable.ic_sg);
            } else {
                ivBendera.setImageResource(R.drawable.ic_bendera);
            }
        } catch (Exception e) {
            // Jika gambar tidak ada, gunakan icon bawaan Android agar tidak crash
            ivBendera.setImageResource(android.R.drawable.ic_menu_help);
        }
    }
}