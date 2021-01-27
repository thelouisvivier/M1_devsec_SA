package com.junia.isen.m1_devsec_sa.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.junia.isen.m1_devsec_sa.model.Account;

import java.util.List;

@Dao
public interface AccountsDao {
    @Query("select * from Account")
    List<Account> getAllAccounts();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);
}
