package com.example.colorplayer.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.colorplayer.model.SongInfo;

import java.util.List;


@Dao
public interface SongInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongInfo(SongInfo songInfo);

    @Delete
    void deleteSongInfo(SongInfo songInfo);

    @Update
    void updateSongInfo(SongInfo songInfo);

    @Query("SELECT * FROM songInfos")
    List<SongInfo> getSongInfos();

    @Query("SELECT * FROM songInfos WHERE id LIKE :songId") // 수정, 삭제시 필요
    SongInfo getSongInfosById(long songId);

    @Query("DELETE FROM songInfos WHERE id LIKE :songId")
    void deleteSongInfosById(long songId);
}
