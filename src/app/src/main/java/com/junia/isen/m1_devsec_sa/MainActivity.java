package com.junia.isen.m1_devsec_sa;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;
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

    // API UTILITIES //
    private UserDatabase uDb;
    private AccountsDatabase accDb;
    public static User myUser;
    public static List<Account> myAccountsList;
    private BankApiService bankApiService;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    private ListView simpleListView;


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
            final Button button = findViewById(R.id.refreshButton);
            fillUi();

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button
                    Toast.makeText(getApplicationContext(),"requête en cours",Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            });
        }
    }


    private void fillUi(){
        final TextView userInfoTextView = findViewById(R.id.userInfo);

        String userToDisplay = myUser.lastname + " " + myUser.name;
        userInfoTextView.setText(userToDisplay);
        simpleListView = (ListView) findViewById(R.id.container);

        List<String> accountsListToDisplay = new ArrayList<>();
        for (Account account : myAccountsList) {
            accountsListToDisplay.add("Compte : " + account.account_name + "\n" + "IBAN : " + account.iban + "\n" + "Solde : " + account.amount + account.currency);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_view, R.id.containerItem, accountsListToDisplay);
        simpleListView.setAdapter(arrayAdapter);
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

        backgroundExecutor.execute(()-> {
            loadFromApiAndSave();
            myUser = uDb.UserDao().getUser();
            myAccountsList = accDb.AccountsDao().getAllAccounts();
            runOnUiThread(() -> {
                fillUi();
            });
        });
    }


    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }





    // ********************************************** //
    // Get data from API then save-it in local DB     //
    // ********************************************** //
    private void loadFromApiAndSave(){
        // Get user + accounts from API
        if(isOnline()){
            try {
                Response<List<Account>> responseAccounts = bankApiService.getAccounts().execute();
                Response<User> responseUser = bankApiService.getUser().execute();
                if(responseAccounts.isSuccessful() && responseUser.isSuccessful()){
                    List<Account> accounts = responseAccounts.body();
                    User user = responseUser.body();

                    // Save accounts in db
                    for(Account account : accounts){
                        accDb.AccountsDao().insert(account);
                    }
                    // save user in db
                    uDb.UserDao().insert(user);
                }
                else{
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,"Erreur de connexion à l'API",Toast.LENGTH_LONG).show(); // Display in toast
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this,"Pas de connexion à internet",Toast.LENGTH_LONG).show(); // Display in toast
            });
        }

    }
}