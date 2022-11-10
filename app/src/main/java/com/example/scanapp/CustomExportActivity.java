package com.example.scanapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;


import com.example.scanapp.databinding.CustomExportActivityBinding;

public class CustomExportActivity extends AppCompatActivity {

    CustomExportActivityBinding binding;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_export_activity);

        binding = CustomExportActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);

        inicializaValores(sharedpreferences);

        binding.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                guardaValores(sharedpreferences);
            }
        });

    }

    private void guardaValores(SharedPreferences sharedpreferences) {

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("SPINNER_CABYS", String.valueOf(binding.spinnerCabys.getSelectedItemPosition()));
        editor.putString("SPINNER_CODBAR", String.valueOf(binding.spinnerCodebar.getSelectedItemPosition()));
        editor.putString("SPINNER_DESC", String.valueOf(binding.spinnerDesc.getSelectedItemPosition()));
        editor.putString("SPINNER_IVA", String.valueOf(binding.spinnerIva.getSelectedItemPosition()));
        editor.putString("SPINNER_CANT", String.valueOf(binding.spinnerCant.getSelectedItemPosition()));
        editor.putString("SPINNER_BODEGA", String.valueOf(binding.spinnerBodega.getSelectedItemPosition()));
        editor.putString("SPINNER_COMPRA", String.valueOf(binding.spinnerCompra.getSelectedItemPosition()));
        editor.putString("SPINNER_VENTA", String.valueOf(binding.spinnerVenta.getSelectedItemPosition()));

        //checkBoxes
        editor.putString("CHECK_CABYS", String.valueOf((binding.chkCabys.isChecked())?'1':'0'));
        editor.putString("CHECK_CODBAR", String.valueOf((binding.chkCodebar.isChecked())?'1':'0'));
        editor.putString("CHECK_DESC", String.valueOf((binding.chkDesc.isChecked())?'1':'0'));
        editor.putString("CHECK_IVA", String.valueOf((binding.chkIva.isChecked())?'1':'0'));
        editor.putString("CHECK_CANT", String.valueOf((binding.chkCant.isChecked())?'1':'0'));
        editor.putString("CHECK_BODEGA", String.valueOf((binding.chkBodega.isChecked())?'1':'0'));
        editor.putString("CHECK_COMPRA", String.valueOf((binding.chkCompra.isChecked())?'1':'0'));
        editor.putString("CHECK_VENTA", String.valueOf((binding.chkVenta.isChecked())?'1':'0'));

        editor.apply();

    }

    private void inicializaValores(SharedPreferences sharedpreferences) {

        binding.chkCabys.setChecked(new String(sharedpreferences.getString("CHECK_CABYS", "1")).equals("1"));
        binding.chkCodebar.setChecked(new String(sharedpreferences.getString("CHECK_CODBAR", "1")).equals("1"));
        binding.chkDesc.setChecked(new String(sharedpreferences.getString("CHECK_DESC", "1")).equals("1"));
        binding.chkIva.setChecked(new String(sharedpreferences.getString("CHECK_IVA", "1")).equals("1"));
        binding.chkCant.setChecked(new String(sharedpreferences.getString("CHECK_CANT", "1")).equals("1"));
        binding.chkBodega.setChecked(new String(sharedpreferences.getString("CHECK_BODEGAC", "1")).equals("1"));
        binding.chkCompra.setChecked(new String(sharedpreferences.getString("CHECK_COMPRA", "1")).equals("1"));
        binding.chkVenta.setChecked(new String(sharedpreferences.getString("CHECK_VENTA", "1")).equals("1"));

        binding.spinnerCabys.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_CABYS","0")));
        binding.spinnerCodebar.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_CODBAR","1")));
        binding.spinnerDesc.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_DESC","2")));
        binding.spinnerIva.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_IVA","3")));
        binding.spinnerCant.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_CANT","4")));
        binding.spinnerBodega.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_BODEGA","5")));
        binding.spinnerCompra.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_COMPRA","6")));
        binding.spinnerVenta.setSelection(Integer.parseInt(sharedpreferences.getString("SPINNER_VENTA","7")));

    }

}
