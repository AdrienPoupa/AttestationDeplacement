package com.poupa.attestationdeplacement.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert
    void insert(ProfileEntity profileEntity);

    @Delete
    void delete(ProfileEntity profileEntity);

    @Update
    void update(ProfileEntity profileEntity);

    @Query("SELECT * FROM profiles")
    List<ProfileEntity> getAll();

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    ProfileEntity find(long id);
}
