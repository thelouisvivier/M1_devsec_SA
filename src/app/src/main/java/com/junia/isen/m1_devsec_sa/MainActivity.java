package com.junia.isen.m1_devsec_sa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    Executor executor; //object that executes submitted Runnable tasks
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prompt at launch
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {

            // Auth succeeded
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show(); // Display in toast
                setContentView(R.layout.activity_main); //set initial view
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
                .setTitle("Vérification par emprunte nécessaire")
                .setDescription("Posez votre doigt")
                .setNegativeButtonText("Exit")
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
}