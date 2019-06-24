package com.italsea.bluetoothle;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

public class Pop extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);

        this.getSupportActionBar().hide();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.1));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // tempo scaduto per il Popup
                finish();
            }
        }, MainActivity.SCAN_PERIOD);


    }
}
