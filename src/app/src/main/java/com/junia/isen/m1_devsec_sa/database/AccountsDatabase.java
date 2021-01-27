package com.junia.isen.m1_devsec_sa.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.junia.isen.m1_devsec_sa.dao.AccountsDao;
import com.junia.isen.m1_devsec_sa.model.Account;

@Database(entities = {Account.class}, version = 1)
public abstract class AccountsDatabase extends RoomDatabase {
    public abstract AccountsDao AccountsDao();
}
