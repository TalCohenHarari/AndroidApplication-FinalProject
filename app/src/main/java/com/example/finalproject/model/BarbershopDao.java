package com.example.finalproject.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BarbershopDao {
    @Query("select * from Barbershop")
    LiveData<List<Barbershop>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Barbershop... barbershops);

    @Delete
    void delete(Barbershop barbershops);
}
