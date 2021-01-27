package com.junia.isen.m1_devsec_sa.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.junia.isen.m1_devsec_sa.dao.UserDao;
import com.junia.isen.m1_devsec_sa.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao UserDao();
}
