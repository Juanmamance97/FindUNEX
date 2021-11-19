package com.example.findunex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.findunex.objects.WAPData;
import com.example.findunex.objects.WAPLocation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {

    JSONObject WapJObject = null;
    JSONObject WapCoords = null;
    public static String json=""; // JSON dado por el server
    public static String jsonCoords="";
    private ArrayList<WAPLocation> geowapList;
    private File jsonFile;

    public double lat;
    public double lon;

    String server_ip = "", url_get_waps = "", url_get_coordinates = ""; //URLs del server
    String url_send_geo = "";

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
            make_url_request_coords();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("JSON", json.toString());
            Log.d("JSON", jsonCoords.toString());
            parserJson(json);
            Toast.makeText(getApplicationContext(), json+"", Toast.LENGTH_LONG).show();
            //showList(distanceWaps);
        }
    }

    class sendWAPLocation extends AsyncTask <Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            server_ip = getResources().getString(R.string.current_ip);
            url_send_geo = getResources().getString(R.string.insert_coords);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpURLConnection urlCon;
                URL url = new URL("http://"+server_ip+url_send_geo);
                Log.d("URL --> ", url+"");
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);
                urlCon.setUseCaches(false);
                urlCon.setRequestProperty("Connection", "keep-alive");
                urlCon.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                urlCon.setRequestProperty("Accept", "*/*");
                urlCon.setRequestProperty("User-Agent", "PostmanRuntime/7.28.4");
                urlCon.setRequestProperty("Content-Type", "application/json");
                urlCon.connect();

                OutputStream os = urlCon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                StringBuilder filetoUpload = readJsonFile(jsonFile.toString());
                writer.write(filetoUpload.toString());
                writer.flush();
                writer.close();

                int response = urlCon.getResponseCode();
                StringBuilder result = new StringBuilder();

                if(response == HttpURLConnection.HTTP_OK){
                    Log.d("RES", "Respuesta correcta");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                    while ((line=br.readLine()) != null){
                        Log.d("RES", line);
                        result.append(line);
                    }
                }
            } catch (Exception e){e.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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

    public void parserJson(String resjson) {
        String sjson = resjson;
        try {
            WapJObject = new JSONObject(sjson);
            JSONArray jsonArray = WapJObject.getJSONArray("WAP");
            geowapList = new ArrayList<>();
            double distanceWAP = 0;
            if(jsonArray.length() > 0){
                for(int i=0; i < jsonArray.length(); i++){
                    // long id = jsonArray.getJSONObject(i).getLong("id");
                    String ssid = jsonArray.getJSONObject(i).getString("ssid");
                    String bssid = jsonArray.getJSONObject(i).getString("bssid");
                    int rssi = jsonArray.getJSONObject(i).getInt("rssi");
                    String timescan = jsonArray.getJSONObject(i).getString("timescan");

                    getCoords(bssid);
                    distanceWAP = calculateDistanceWAP(rssi);
                    //distanceWaps.add(distanceWAP);
                    Log.d("OBJ", String.valueOf(lat));
                    Log.d("OBJ", String.valueOf(lon));
                    WAPLocation waploc = new WAPLocation(bssid, lat, lon, distanceWAP);
                    geowapList.add(waploc);
                }
                writeJSONFile(geowapList);
                new sendWAPLocation().execute();
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public double calculateDistanceWAP(int rssi){
        int txPower = -42; //Valor constante de la intensidad de la señal esperada a 1m del WAP en dBm
        double n = 3; //Valor constante dependiente del factor ambiental
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

    public void getCoords(String bssid){
        try {
            WapCoords = new JSONObject(jsonCoords);
            JSONArray jsonArrayC = WapCoords.getJSONArray("COORDS");
            boolean enc = false;
            if(jsonArrayC.length() > 0){
                for(int i=0; i < jsonArrayC.length()&&!enc; i++){
                    String bssid_coords = jsonArrayC.getJSONObject(i).getString("bssid");
                    Log.d("WAP", bssid);
                    Log.d("COORDSWAP", bssid_coords);
                    if(bssid.equals(bssid_coords)){
                        enc = true;
                        lat = jsonArrayC.getJSONObject(i).getDouble("latitud");
                        lon = jsonArrayC.getJSONObject(i).getDouble("longitud");
                        Log.d("OBJ", "====================");
                        Log.d("OBJ", bssid_coords);
                      //  Log.d("OBJ", String.valueOf(lat));
                     //   Log.d("OBJ", String.valueOf(lon));
                    } else {
                        lat = 0.0;
                        lon = 0.0;
                    }
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public void writeJSONFile(ArrayList<WAPLocation> listwaps){
        File rootFolder = this.getExternalFilesDir(null);
        jsonFile = new File(rootFolder, "Geowaps.json");
        FileWriter writer = null;
        try {
            writer = new FileWriter(jsonFile);
            writer.write("[\n");

            for(int i = 0; i < listwaps.size(); i++){
                writer.write("{\n");
                writer.write('"'+"bssid"+'"'+":"+'"'+listwaps.get(i).getBssid()+'"'+",\n");
                writer.write('"'+"latitud"+'"'+":"+'"'+listwaps.get(i).getLatitud()+'"'+",\n");
                writer.write('"'+"longitud"+'"'+":"+'"'+listwaps.get(i).getLongitud()+'"'+",\n");
                writer.write('"'+"distance"+'"'+":"+'"'+listwaps.get(i).getDistance()+'"'+"\n");
                if(i == listwaps.size()-1){
                    writer.write("}\n");
                } else {
                    writer.write("},\n");
                }
            }
            writer.write("]");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder readJsonFile(String file){
        StringBuilder sb = new StringBuilder();
        String line;
        File f = new File(file);
        FileReader fr;
        BufferedReader br = null;
        try {
            try {
                fr = new FileReader(f);
                br = new BufferedReader(fr);
                while((line = br.readLine()) != null){
                    sb.append(line);
                    sb.append('\n');
                }
            } finally {
                if(br != null){
                    br.close();
                }
            }
        } catch (FileNotFoundException e){
            Log.d("Error", "Fichero no encontrado");
        } catch (IOException e){
            e.printStackTrace();
            Log.d("Error", "Error entrada/salida");
        }
        return sb;
    }
}