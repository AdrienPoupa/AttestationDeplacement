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

    /**
     * Returns all the profile entities save in the database
     *
     * @return profile entities in a LiveData
     */
    @Query("SELECT * FROM profiles")
    List<ProfileEntity> getAll();
}
