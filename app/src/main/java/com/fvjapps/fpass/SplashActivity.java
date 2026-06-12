package com.fvjapps.fpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;

public class SplashActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAndAuthenticate();
    }

    private void checkAndAuthenticate() {
        BiometricManager manager = BiometricManager.from(this);
        int canAuthenticate = manager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                        | BiometricManager.Authenticators.DEVICE_CREDENTIAL
        );

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt();
        } else {
            String message;
            if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
                message = "No biometric hardware available on this device";
            } else if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
                message = "Biometric hardware is currently unavailable";
            } else if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                message = "No biometric credentials enrolled. Please set up a device lock screen or fingerprint";
            } else {
                message = "Authentication is not available on this device";
            }
            showErrorAndExit(message);
        }
    }

    private void showBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(SplashActivity.this, ThemeDemoActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showErrorAndExit("Please authenticate to access the application");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showErrorAndExit("Please authenticate to access the application");
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("App Authentication")
                .setSubtitle("Verify your identity to access fpass")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                                | BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void showErrorAndExit(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(() -> {
            finishAffinity();
        }, 2000);
    }
}
