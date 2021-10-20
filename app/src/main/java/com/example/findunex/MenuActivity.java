package com.example.findunex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button btn_scaner;
    private Button btn_location;
    private Button btn_showmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_scaner = findViewById(R.id.btn_scanposicion);
        btn_location = findViewById(R.id.btn_location);
        btn_showmap = findViewById(R.id.btn_mostrarmapa);

        btn_scaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListWifiScannerActivity.class);
                startActivity(intent);
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

        btn_showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}