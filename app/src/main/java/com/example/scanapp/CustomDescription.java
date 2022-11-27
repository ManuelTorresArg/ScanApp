package com.example.scanapp;

import android.app.Activity;
import android.content.Intent;
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
            rdbtn.setText(descripciones[i].toString().toUpperCase());
            binding.descriptionGroup.addView(rdbtn);
        }

        binding.btnCustomAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedButton = binding.descriptionGroup.getCheckedRadioButtonId();
                View radioButton = binding.descriptionGroup.findViewById(selectedButton);
                int idx = binding.descriptionGroup.indexOfChild(radioButton);

                RadioButton r = (RadioButton) binding.descriptionGroup.getChildAt(idx);
                String selectedtext = r.getText().toString();


                Intent returnIntent = new Intent();
                returnIntent.putExtra("descripcion",selectedtext);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();



            }
        });



    }

}
