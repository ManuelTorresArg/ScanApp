package com.example.scanapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.ConfigActivityBinding;

public class ConfigActivity extends AppCompatActivity {

    ConfigActivityBinding binding;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.config_activity);

        binding = ConfigActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);

        inicializaValores(sharedpreferences);

        binding.configBtnConfigCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(ConfigActivity.this,CustomExportActivity.class);
                ConfigActivity.this.startActivity(myIntent);

            }
        });

        binding.configBtnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                guardaValores(sharedpreferences);
            }
        });

        binding.configBtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void guardaValores(SharedPreferences sharedpreferences) {

        SharedPreferences.Editor editor = sharedpreferences.edit();

        //Cheackboxes
        editor.putString("CHECK_CONTINUO", String.valueOf((binding.chkContinuo.isChecked())?'1':'0'));
        editor.putString("CHECK_SUMARIZA", String.valueOf((binding.chkSumariza.isChecked())?'1':'0'));

        //RadioGroups
        editor.putString("RADIO_GROUP_SISTEMA",String.valueOf(binding.radioButtonVentas.isChecked()?'0': (binding.radioButtonAvanza.isChecked() ? '1' : '2')));
        editor.putString("RADIO_GROUP_TIPO",String.valueOf(binding.radioButtonCarpeta.isChecked()?'0':'1'));
        editor.putString("RADIO_GROUP_UBICACION",String.valueOf(binding.radioButtonDocumentos.isChecked()?'0':'1'));

        editor.apply();


    }

    private void inicializaValores(SharedPreferences sharedpreferences) {

        binding.chkContinuo.setChecked(new String(sharedpreferences.getString("CHECK_CONTINUO", "1")).equals("1"));
        binding.chkSumariza.setChecked(new String(sharedpreferences.getString("CHECK_SUMARIZA", "1")).equals("1"));

        binding.radioButtonVentas.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_SISTEMA", "0")).equals("0"));
        binding.radioButtonAvanza.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_SISTEMA", "0")).equals("1"));
        binding.radioButtonCustom.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_SISTEMA", "0")).equals("2"));

        binding.radioButtonCarpeta.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_TIPO", "0")).equals("0"));
        binding.radioButtonCompartir.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_TIPO", "0")).equals("1"));

        binding.radioButtonDocumentos.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_UBICACION", "0")).equals("0"));
        binding.radioButtonDescargas.setChecked(new String(sharedpreferences.getString("RADIO_GROUP_UBICACION", "0")).equals("1"));


    }

}
