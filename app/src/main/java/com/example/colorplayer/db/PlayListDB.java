package com.example.colorplayer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.colorplayer.model.PlayList;
import com.example.colorplayer.model.SongInfo;


@Database(entities = PlayList.class, version = 1)
public abstract class PlayListDB extends RoomDatabase {

    public abstract PlayListDao playListDao();

    public static final String DATABASE_NAME = "playListDb";
    private static PlayListDB instance;

    public static PlayListDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, PlayListDB.class, DATABASE_NAME)
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
