package com.example.findunex.objects;

public class WAPLocation {
    private String bssid;
    private double latitud;
    private double longitud;
    private double distance;

    public WAPLocation() {
        this.bssid = "";
        this.latitud = 0.0;
        this.longitud = 0.0;
        this.distance = 0.0;
    }

    public WAPLocation(String bssid, double latitud, double longitud, double distance) {
        this.bssid = bssid;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distance = distance;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
