package com.junia.isen.m1_devsec_sa.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.junia.isen.m1_devsec_sa.model.User;

@Dao
public interface UserDao {
    @Query("select * from User")
    User getUser();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
}
