package com.example.scanapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.ConfigActivityBinding;

public class ConfigActivity extends AppCompatActivity {

    ConfigActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.config_activity);

        binding = ConfigActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }

}
