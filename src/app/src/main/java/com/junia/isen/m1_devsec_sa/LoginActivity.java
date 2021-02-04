package com.junia.isen.m1_devsec_sa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    // AUTH UTILITIES //



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            setContentView(R.layout.activity_login); //set auth view
            final Button loginButton = findViewById(R.id.login);

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final EditText passwordTextView = findViewById(R.id.password);
                    if(passwordTextView.getText().toString().length() < 8){
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Mot de passe trop court !")
                                .setMessage("Votre mot de passe doit faire plus de 8 caractères.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                    else {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.putExtra("password", passwordTextView.getText().toString());
                        startActivity(intent);
                    }
                }
            });
        }

    }
}