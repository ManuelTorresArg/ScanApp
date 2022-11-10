package com.example.scanapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.example.scanapp.databinding.MainActivityBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_activity);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        Toast.makeText(MainActivity.this, "Search!!", Toast.LENGTH_SHORT).show();
                        Log.d("Announcement", "Received Search");
                        break;
                    case R.id.config:
                        Intent myIntent = new Intent(MainActivity.this,ConfigActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                }


                return false;
            }
        });

        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        MainActivity.this
                );
                //setear pregunta
                intentIntegrator.setPrompt("Para flash use volumen arriba");
                //Seter beep
                intentIntegrator.setBeepEnabled(true);
                //lock orientación
                intentIntegrator.setOrientationLocked(true);
                //Setear actividad de captura
                intentIntegrator.setCaptureActivity(Capture.class);
                //Iniciar Scan
                intentIntegrator.initiateScan();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Inicializamos resultado pero solo si recibo data desde la activity EDIT verificando el EDIT_ACTIVITY_REQUEST_CODE que le definimos

        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        //Toma en contenido de el intent y lo pone en el edittext
        if (intentResult.getContents() != null) {

            Toast.makeText(this, intentResult.getContents(), Toast.LENGTH_SHORT).show();

        } else {
            //Cuando el contenido del resultado es null
            Toast.makeText(getApplicationContext(), "Error, contenido nulo", Toast.LENGTH_SHORT).show();
        }

    }

}
