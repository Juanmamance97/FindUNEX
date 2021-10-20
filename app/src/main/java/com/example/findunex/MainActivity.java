package com.example.findunex;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.example.findunex.room_db.FindUNEXdatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private Button btn_inicio;
    private String Prueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_inicio = findViewById(R.id.btn_inicio);

        btn_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);

            }
        });
        FindUNEXdatabase.getInstance(this);
        checkPermissions();
       /* Prueba = "Prueba";
        try {
            save(this, Prueba);
            Log.d("Fichero", "Creado");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Fichero", "No creado");
        }*/
    }

    /*public void save(Context context, String jsonString) throws IOException {
        File rootFolder = context.getExternalFilesDir(null);
        File jsonFile = new File(rootFolder, "waps.json");
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(jsonString);
        writer.close();
        Log.d("Fichero", jsonFile.getAbsolutePath());
        //or IOUtils.closeQuietly(writer);
    }*/

    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }
}