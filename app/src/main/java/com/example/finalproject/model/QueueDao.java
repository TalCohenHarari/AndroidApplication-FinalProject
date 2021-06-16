package com.example.finalproject.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QueueDao {
    @Query("select * from Queue")
    LiveData<List<Queue>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Queue... queues);

    @Delete
    void delete(Queue queues);
}
