package com.junia.isen.m1_devsec_sa.api;

import com.junia.isen.m1_devsec_sa.model.Account;
import com.junia.isen.m1_devsec_sa.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BankApiService {
    @GET("accounts")
    Call<List<Account>> getAccounts();

    @GET("config/1")
    Call<User> getUser();
}
