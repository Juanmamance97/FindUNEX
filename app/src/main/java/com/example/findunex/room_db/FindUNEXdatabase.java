package com.example.findunex.room_db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.findunex.objects.WAPData;

@Database(entities = {WAPData.class}, version = 1)

public abstract class FindUNEXdatabase extends RoomDatabase {
    private static FindUNEXdatabase instance;

    public static FindUNEXdatabase getInstance(Context context){
        if(instance == null)
            instance = Room.databaseBuilder(context, FindUNEXdatabase.class, "FindUNEX.db").build();
        return instance;
    }

    public abstract WAPDataDao getDao();
}
