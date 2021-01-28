package com.junia.isen.m1_devsec_sa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.junia.isen.m1_devsec_sa.api.BankApiService;
import com.junia.isen.m1_devsec_sa.database.AccountsDatabase;
import com.junia.isen.m1_devsec_sa.database.UserDatabase;
import com.junia.isen.m1_devsec_sa.model.Account;
import com.junia.isen.m1_devsec_sa.model.User;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    // AUTH UTILITIES //
    Executor executor; //object that executes submitted Runnable tasks
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    private boolean authorized = false;

    // API UTILITIES //
    private UserDatabase uDb;
    private AccountsDatabase accDb;
    public static User myUser;
    public static List<Account> myAccountsList;
    private BankApiService bankApiService;
    private Executor backgroundExecutor = Executors.newSingleThreadExecutor();






    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ask for auth
        askForAuth(savedInstanceState);
    }







    // ********************************************** //
    // Start Ui and display data                      //
    // ********************************************** //
    private void startUi(Bundle savedInstanceState){
        if(savedInstanceState == null){
            setContentView(R.layout.activity_main); //set initial view
            final Button button = findViewById(R.id.mybutton);
            final TextView text = findViewById(R.id.patate);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    Toast.makeText(getApplicationContext(),"requête en cours",Toast.LENGTH_SHORT).show();
                    refreshData();
                    text.setText((CharSequence) myUser.lastname + myUser.name);
                }
            });




            // display accounts and user
        }
    }




    // ********************************************** //
    // Ask user for auth to enter app                 //
    // ********************************************** //
    private void askForAuth (Bundle savedInstanceState){
        // Prompt at launch
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {

            // Auth succeeded
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show(); // Display in toast
                startBackgroundThread(savedInstanceState);
            }

            // Auth error
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Toast.makeText(MainActivity.this,errString,Toast.LENGTH_LONG).show(); // Display err in toast
                MainActivity.this.finish(); // Quit app
            }

            // Auth failed
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Popup failure
                Toast.makeText(MainActivity.this,"Failed AUTH",Toast.LENGTH_LONG).show(); // Display in toast
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Vérification par empreinte nécessaire")
                .setDescription("Posez votre doigt")
                .setNegativeButtonText("Exit")
                .setDeviceCredentialAllowed(false)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }






    // ********************************************** //
    // Start bg thread to get data then start app     //
    // ********************************************** //
    private void startBackgroundThread(Bundle savedInstanceState){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bankApiService = retrofit.create(BankApiService.class);

        //New thread pour la bdd
        backgroundExecutor.execute(()-> {
            uDb = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "user_database.db").build();
            accDb = Room.databaseBuilder(getApplicationContext(), AccountsDatabase.class, "accounts_database.db").build();
        });


        backgroundExecutor.execute(()-> {
            loadFromApiAndSave();
            myUser = uDb.UserDao().getUser();
            myAccountsList = accDb.AccountsDao().getAllAccounts();

            runOnUiThread(() -> {
                // start ui
                startUi(savedInstanceState);
            });
        });
    }

    // ********************************************** //
    // Start bg thread to get data then start app     //
    // ********************************************** //
    private void refreshData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://6007f1a4309f8b0017ee5022.mockapi.io/api/m1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        bankApiService = retrofit.create(BankApiService.class);

        //New thread pour la bdd
        backgroundExecutor.execute(()-> {
            uDb = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "user_database.db").build();
            accDb = Room.databaseBuilder(getApplicationContext(), AccountsDatabase.class, "accounts_database.db").build();
        });


        backgroundExecutor.execute(()-> {
            loadFromApiAndSave();
            myUser = uDb.UserDao().getUser();
            myAccountsList = accDb.AccountsDao().getAllAccounts();
        });
    }





    // ********************************************** //
    // Get data from API then save-it in local DB     //
    // ********************************************** //
    private void loadFromApiAndSave(){
        // Get user + accounts from API
        try {
            Response<List<Account>> responseAccounts = bankApiService.getAccounts().execute();
            Response<User> responseUser = bankApiService.getUser().execute();
            if(responseAccounts.isSuccessful() && responseUser.isSuccessful()){
                List<Account> accounts = responseAccounts.body();
                User user = responseUser.body();
                Log.w("Bank APP","Accounts: "+ accounts.size());

                // Save accounts in db
                for(Account account : accounts){
                    accDb.AccountsDao().insert(account);
                }
                // save user in db
                uDb.UserDao().insert(user);
            }
            else{
                Log.w("Bank APP","resquest error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}