package com.example.findunex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.findunex.objects.WAPData;
import com.example.findunex.room_db.FindUNEXdatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ListWifiScannerActivity extends AppCompatActivity {

    private ListView listWifi;
    private Button btn_scan;
    private WifiManager wifiManager;
    private List<ScanResult> wifiData;
    private ArrayList<String> results = new ArrayList<>();
    private ArrayAdapter adapter;

    private String WAP_SSID;
    private String WAP_BSSID;
    private int WAP_RSSI;
    private String actualdateTimestamp;

    String server_ip = "", url_insert_waps = "";
    String response = "";

    private ArrayList<WAPData> wapList;
   // private JSONArray resultsJSON;
  //  private JSONObject obj;

    Handler handler = new Handler();
    private final int TIME = 30000;
    private File jsonFile;
    public static String responseServer="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wifi_scanner);

        btn_scan = findViewById(R.id.btn_scanner);
        listWifi = findViewById(R.id.listWifiScanner);

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiFiScan();
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            //Mensaje de aviso de WiFi desactivado
            Toast.makeText(this, "Conexión WiFi desactivada", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listWifi.setAdapter(adapter);
        WiFiScan();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanEjecute();
    }

    private void ScanEjecute(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WiFiScan();
                handler.postDelayed(this, TIME);
            }
        }, TIME);
    }

    private void WiFiScan() {
        results.clear();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FindUNEXdatabase wapdatabase = FindUNEXdatabase.getInstance(getApplicationContext());
                wapdatabase.getDao().deleteAllwaps();
            }
        });
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Escaneando WiFi...", Toast.LENGTH_SHORT).show();
    }

    Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult o1, ScanResult o2) {
            return (o1.level > o2.level ? -1 : (o1.level==o2.level ? 0 : 1));
        }
    };

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiData = wifiManager.getScanResults();
            unregisterReceiver(this);
            Collections.sort(wifiData, comparator);

            wapList = new ArrayList<>();

          //  resultsJSON = new JSONArray();
          //  obj = new JSONObject();

            for (ScanResult scanResult : wifiData) {
                WAP_SSID = scanResult.SSID;
                WAP_BSSID = scanResult.BSSID;
                WAP_RSSI = scanResult.level;

                long actualTimestamp = System.currentTimeMillis() - SystemClock.elapsedRealtime() + (scanResult.timestamp / 1000);
                Date actualDate = new Date(actualTimestamp);
                actualdateTimestamp = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(actualDate);

                WAPData wap = new WAPData();

                wap.setSsid(WAP_SSID);
                wap.setBssid(WAP_BSSID);
                wap.setRssi(WAP_RSSI);
                wap.setTimescan(actualdateTimestamp);

                wapList.add(wap);
                Log.d("Lista", wapList.size()+"");

               /*Gson gson = new Gson();
                String jsonObject = gson.toJson(wap);
                JsonObject convertObject = new Gson().fromJson(jsonObject, JsonObject.class);
                Log.d("JsonObject", convertObject+"");*/

                //resultsJSON.put(convertObject);
               /* Log.d("JsonArray", resultsJSON+"");
                try {
                    obj.put("wap", resultsJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("JsonArray", obj+"");*/

                /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        FindUNEXdatabase wapdatabase = FindUNEXdatabase.getInstance(getApplicationContext());
                        long id_wap = wapdatabase.getDao().insert(wap);
                        wap.setId(id_wap);
                    }
                });*/

                results.add(WAP_SSID + "("+ WAP_BSSID +")"+ ": " + WAP_RSSI + " dBm" + " - " +actualdateTimestamp);

                adapter.notifyDataSetChanged();
            }

            writeJSONFile(wapList);
           // String strJSON = resultsJSON.toString();

           /* Log.d("Repu", strJSON);
            try {
                Log.d("Fichero", "Escribe2");
               // writer.write(resultsJSON.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.d("Fichero", "Escribe3");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Fichero", jsonFile.getAbsolutePath());*/
            //Envío de los datos a FindUnexdb
            new InsertSyncWAP().execute();

        }
    };

    class InsertSyncWAP extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            server_ip = getResources().getString(R.string.current_ip);
            url_insert_waps = getResources().getString(R.string.insert_waps);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://"+server_ip+url_insert_waps);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ssid", "MIWIFI_Hk2A");
                jsonObject.put("bssid", "64:13:6c:25:a9:cc");
                jsonObject.put("rssi", "-87");
                jsonObject.put("timescan", "18:46:45 08-10-2021");
                String message = jsonObject.toString();
                Log.d("OBJ", message);

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(message.getBytes().length);

                conn.setRequestProperty("Content-Type","application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                conn.connect();

                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                //clean up
                os.flush();

                is = conn.getInputStream();

                //Respuesta del servidor
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.d("HTTP response is: ", serverResponseCode + " - " + serverResponseMessage);

            } catch (IOException e){e.printStackTrace();
            } catch (JSONException e){e.printStackTrace();
            } finally {
                //clean up
                try {
                    os.close();
                    is.close();
                } catch (IOException e){e.printStackTrace();}
                conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("response=",response+"");
            Toast.makeText(getApplicationContext(), "WAP insertado", Toast.LENGTH_SHORT).show();
        }
    }

    public void writeJSONFile(ArrayList<WAPData> listwaps){
        File rootFolder = this.getExternalFilesDir(null);
        jsonFile = new File(rootFolder, "waps.json");
        Log.d("Fichero", jsonFile.getAbsolutePath());
        FileWriter writer = null;
        try {
            Log.d("Fichero", "Escribe");
            writer = new FileWriter(jsonFile);
            writer.write("[\n");

            for(int i = 0; i < listwaps.size(); i++){
                writer.write("{\n");
                writer.write('"'+"ssid"+'"'+":"+'"'+listwaps.get(i).getSsid()+'"'+",\n");
                writer.write('"'+"bssid"+'"'+":"+'"'+listwaps.get(i).getBssid()+'"'+",\n");
                writer.write('"'+"rssi"+'"'+":"+'"'+listwaps.get(i).getRssi()+'"'+",\n");
                writer.write('"'+"timescan"+'"'+":"+'"'+listwaps.get(i).getTimescan()+'"'+"\n");
                if(i == listwaps.size()-1){
                    writer.write("}\n");
                } else {
                    writer.write("},\n");
                }
                Log.d("Fichero", "Escribe2");
            }
            writer.write("]");
            Log.d("Fichero", "Escribe3");
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