package com.example.scanapp;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.DescriptionSelectActivityBinding;

public class CustomDescription extends AppCompatActivity {

    DescriptionSelectActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description_select_activity);

        binding = DescriptionSelectActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String[] descripciones = getIntent().getStringArrayExtra("DESCRIPCIONES");

       //getIntent().getStringExtra("DESCRIPCIONES");

        for (int i = 0; i < descripciones.length; i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(View.generateViewId());
            rdbtn.setText("Radio " + descripciones[i].toString());
            binding.descriptionGroup.addView(rdbtn);
        }



    }

}
