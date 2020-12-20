package com.example.teste4vets.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.teste4vets.R;

public class AberturaActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {

    }

    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                Intent i = new Intent(AberturaActivity.this, LoginActivity.class);
                startActivity(i);

                // Fecha esta activity
                finishAffinity();
            }
        }, SPLASH_TIME_OUT);
    }
}