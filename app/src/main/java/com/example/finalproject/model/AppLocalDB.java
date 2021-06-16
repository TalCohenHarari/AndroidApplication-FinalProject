package com.example.finalproject.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finalproject.MyApplication;


@Database(entities = {User.class,Barbershop.class,Queue.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract BarbershopDao BarbershopDao();
    public abstract QueueDao QueueDao();
}

public class AppLocalDB{
    final static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDbRepository.class,
                    "dbBestBarbershops.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

