package com.example.findunex.room_db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.findunex.objects.WAPData;

import java.util.List;

@Dao
public interface WAPDataDao {
    @Query("SELECT * FROM waps")
    public List<WAPData> getAll();

   // @Query("SELECT * FROM waps where ssid=(:ssid) and bssid=(:bssid)")
   // WAPData selectOne(String ssid, String bssid);

    @Insert
    public long insert(WAPData wapDataJSON);

    @Query("DELETE FROM waps")
    public void deleteAllwaps();
}
