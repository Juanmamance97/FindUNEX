package com.example.findunex.objects;

import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "waps")

public class WAPData {

    @Ignore
    public final static String SSID = "ssid";
    @Ignore
    public final static String BSSID = "bssid";
    @Ignore
    public final static String RSSI = "rssi";
    @Ignore
    public final static String TimeScan = "timescan";

    @PrimaryKey (autoGenerate = true)
    private long id;
    @ColumnInfo(name = "ssid")
    private String ssid;
    @ColumnInfo(name = "bssid")
    private String bssid;
    @ColumnInfo(name = "rssi")
    private Integer rssi;
    @ColumnInfo(name = "timescan")
    private String timescan;

    @Ignore
    public WAPData() {
        this.ssid = "";
        this.bssid = "";
        this.rssi = 0;
        this.timescan = "";
    }

    @Ignore
    public WAPData(String ssid, String bssid, Integer rssi, String timescan) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.timescan = timescan;
    }

    @Ignore
    public WAPData (Intent intent){
        ssid = intent.getStringExtra(WAPData.SSID);
        bssid = intent.getStringExtra(WAPData.BSSID);
        rssi = intent.getIntExtra(WAPData.RSSI, 0);
        timescan = intent.getStringExtra(WAPData.TimeScan);
    }

    public WAPData(long id, String ssid, String bssid, Integer rssi, String timescan) {
        this.id = id;
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.timescan = timescan;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public String getTimescan() {
        return timescan;
    }

    public void setTimescan(String timescan) {
        this.timescan = timescan;
    }

    public static void packageIntent(Intent intent, String mssid, String mbssid, Integer mrssi, String mtimescan){
        intent.putExtra(WAPData.SSID, mssid);
        intent.putExtra(WAPData.BSSID, mbssid);
        intent.putExtra(WAPData.RSSI, mrssi);
        intent.putExtra(WAPData.TimeScan, mtimescan);
    }
}
