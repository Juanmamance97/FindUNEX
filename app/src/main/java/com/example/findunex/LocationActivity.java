package com.example.findunex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

public class LocationActivity extends AppCompatActivity {

    Handler handler = new Handler();
    private final int TIME = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Sync_WAPS sync_waps = new Sync_WAPS(LocationActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestWAPs();
    }

    private void requestWAPs(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Sync_WAPS sync_waps = new Sync_WAPS(LocationActivity.this);
                handler.postDelayed(this, TIME);
            }
        }, TIME);
    }
}