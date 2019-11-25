package com.example.colorplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.colorplayer.model.SongInfo;


@Database(entities = SongInfo.class, version = 1)
public abstract class SongInfoDB extends RoomDatabase {

    public abstract SongInfoDao songInfoDao();

    public static final String DATABASE_NAME = "moviesDb";
    private static SongInfoDB instance;

    public static SongInfoDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, SongInfoDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void closeInstance(){

        if(instance != null){
            instance.getOpenHelper().close();
        }
    }
}
