package com.example.colorplayer.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.colorplayer.model.PlayList;

import java.util.List;


@Dao
public interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayList(PlayList playList);

    @Delete
    void deletePlayList(PlayList playList);

    @Update
    void updatePlayList(PlayList playList);

    @Query("SELECT * FROM playList")
    List<PlayList> getPlayList();

    @Query("SELECT * FROM playList WHERE id LIKE :id") // 수정, 삭제시 필요
    PlayList getPlayListById(long id);

    @Query("DELETE FROM playList WHERE id LIKE :id")
    void deletePlayListById(long id);
}
