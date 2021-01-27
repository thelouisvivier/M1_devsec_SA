package com.junia.isen.m1_devsec_sa.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public Integer id;

    public String name;
    public String lastname;
}
