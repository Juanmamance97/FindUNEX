package com.example.findunex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.findunex.objects.WAPData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Watchable;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    JSONObject WapJObject = null;
    JSONObject WapCoords = null;
    public static String json=""; // JSON dado por el server
    public static String jsonCoords="";
    ArrayList<Double> distanceWaps; //Lista de objetos WAP obtenidos del JSON

    String server_ip = "", url_get_waps = "", url_get_coordinates = ""; //URLs del server

    Handler handler = new Handler();
    private final int TIME = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        //Pedir JSON del server
        new ParseSyncWAP().execute();
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
                //Pedir JSON del server auto cada 30 seg
                new ParseSyncWAP().execute();
                handler.postDelayed(this, TIME);
            }
        }, TIME);
    }

    class ParseSyncWAP extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            server_ip = getResources().getString(R.string.current_ip);
            url_get_waps = getResources().getString(R.string.get_waps);
            url_get_coordinates = getResources().getString(R.string.get_coords);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            make_url_request_waps();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parserJson(json);
            Toast.makeText(getApplicationContext(), json+"", Toast.LENGTH_LONG).show();
            showList(distanceWaps);
        }
    }

    public ArrayList<Double> getdistanceWaps(){
        return distanceWaps;
    }

    void make_url_request_waps(){
        try {
            String url = "http://"+server_ip+url_get_waps;
            Log.d("URL --> ", url+"");
            HttpHandler httpHandler = new HttpHandler();
            json = httpHandler.makeServiceCall(url);

        } catch (Exception e){e.printStackTrace(); Log.e("error", e+"");}
    }

    void make_url_request_coords(){
        try {
            String url = "http://"+server_ip+url_get_coordinates;
            Log.d("URL --> ", url+"");
            HttpHandler httpHandler = new HttpHandler();
            jsonCoords = httpHandler.makeServiceCall(url);
        } catch (Exception e){e.printStackTrace();}
    }

    public void parserJson(String refjson) {
        String sJson = refjson;
        try {
            WapJObject = new JSONObject(sJson);
            JSONArray jsonArray = WapJObject.getJSONArray("WAP");
            distanceWaps = new ArrayList<>();
            double distanceWAP = 0;
            if(jsonArray.length() > 0){
                for(int i=0; i < jsonArray.length(); i++){
                   // long id = jsonArray.getJSONObject(i).getLong("id");
                    String ssid = jsonArray.getJSONObject(i).getString("ssid");
                    String bssid = jsonArray.getJSONObject(i).getString("bssid");
                    int rssi = jsonArray.getJSONObject(i).getInt("rssi");
                    String timescan = jsonArray.getJSONObject(i).getString("timescan");
                    
                   // WAPData wapData = new WAPData();
                  //  wapData.setId(id);
                   // wapData.setSsid(ssid);
                  //  wapData.setBssid(bssid);
                  //  wapData.setRssi(rssi);
                   // wapData.setTimescan(timescan);
                  //  wapsreceived.add(wapData);

                    WapCoords = new JSONObject(jsonCoords);
                    JSONArray jsonArrayCoords = WapCoords.getJSONArray("COORDS");
                    boolean enc = false;
                    for(int j=0; j < jsonArrayCoords.length() || !enc; j++) {
                        String bssid_coords = jsonArrayCoords.getJSONObject(j).getString("bssid");
                        if(bssid_coords == bssid){
                            enc = true;
                            double lat = jsonArrayCoords.getJSONObject(j).getDouble("latitud");
                            double lon = jsonArrayCoords.getJSONObject(j).getDouble("longitud");
                            Log.d("OBJ", "====================");
                            Log.d("OBJ", bssid);
                            Log.d("OBJ", String.valueOf(lat));
                            Log.d("OBJ", String.valueOf(lon));
                        }
                    }

                    distanceWAP = calculateDistanceWAP(rssi);
                    distanceWaps.add(distanceWAP);
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public double calculateDistanceWAP(int rssi){
        int txPower = -32; //Valor constante de la intensidad de la señal esperada a 1m del WAP en dBm
        double n = 2.5; //Valor constante dependiente del factor ambiental
        double result = 0.0;

        if(rssi == 0){
            result = -1.0;
        }
        else {
            double power = (txPower - rssi) / (10 * n);
            result = Math.pow(10, power);
        }
        return result;
    }

    public void showList(ArrayList<Double> listDistance){
        double distance;
        for(int i = 0; i<listDistance.size(); i++){
            distance = listDistance.get(i);
            Log.d("Distancias", "Dist"+i+" = "+distance);
        }
    }

    //Hilo para obtener las coordenadas
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                make_url_request_coords();
            } catch (Exception e){e.printStackTrace();}
        }
    });
}