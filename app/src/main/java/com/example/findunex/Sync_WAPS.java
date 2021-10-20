package com.example.findunex;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class Sync_WAPS {

    InputStream is = null;
    JSONObject WapJObject = null;
    public static String json="";
    JsonObject getobject = null;

    String server_ip = "", url_get_waps = "";
    Context ctx;

    Sync_WAPS(Context ctx) {
        this.ctx = ctx;
        server_ip = ctx.getResources().getString(R.string.current_ip);
        url_get_waps = ctx.getResources().getString(R.string.get_waps);

        new ParseSyncWAP().execute();
    }

    class ParseSyncWAP extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            make_url_request();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parserJson(json);
            Toast.makeText(ctx, json+"", Toast.LENGTH_LONG).show();
        }
    }

    void make_url_request(){
        try {
            String url = "http://"+server_ip+url_get_waps;
            Log.d("URL --> ", url+"");
            HttpHandler httpHandler = new HttpHandler();
            json = httpHandler.makeServiceCall(url);

        } catch (Exception e){e.printStackTrace(); Log.e("error", e+"");}
    }

    public void parserJson(String refjson) {
        String sJson = refjson;
        try {
            WapJObject = new JSONObject(sJson);
            JSONArray jsonArray = WapJObject.getJSONArray("WAP");
            if(jsonArray.length() > 0){
                for(int i=0; i < jsonArray.length(); i++){
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String ssid = jsonArray.getJSONObject(i).getString("ssid");
                    String bssid = jsonArray.getJSONObject(i).getString("bssid");
                    String rssi = jsonArray.getJSONObject(i).getString("rssi");
                    String timescan = jsonArray.getJSONObject(i).getString("timescan");

                    //Comprobación (BORRAR LUEGO)
                    Log.d("==========", "==========");
                    Log.d("id", id+"");
                    Log.d("ssid", ssid+"");
                    Log.d("bssid", bssid+"");
                    Log.d("rssi", rssi+"");
                    Log.d("timescan", timescan+"");
                }
            }

        } catch (Exception e){e.printStackTrace();}
    }
}
