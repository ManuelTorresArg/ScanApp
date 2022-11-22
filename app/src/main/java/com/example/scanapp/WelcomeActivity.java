package com.example.scanapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.WelcomeActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    WelcomeActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);


        //Creamos las instancias de sesion y usuario
        FirebaseAuth LoginActivo = FirebaseAuth.getInstance();
        FirebaseUser ActiveUser = LoginActivo.getCurrentUser();

        binding = WelcomeActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.activeUser.setText(ActiveUser.getEmail());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent myIntent = new Intent(WelcomeActivity.this,MainActivity.class);
                WelcomeActivity.this.startActivity(myIntent);
                finish();
            }
        }, 2000);   //5 seconds
    }

}
