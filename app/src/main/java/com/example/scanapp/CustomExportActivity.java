package com.example.scanapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;


import androidx.appcompat.app.AppCompatActivity;


import com.example.scanapp.databinding.CustomExportActivityBinding;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class CustomExportActivity extends AppCompatActivity {

    CustomExportActivityBinding binding;

    SharedPreferences sharedpreferences;

    Set<Spinner> misSpinners = new LinkedHashSet<>();

    Integer actual1,actual2,actual3,actual4, actual5, actual6, actual7, actual8, previo1, previo2, previo3, previo4, previo5, previo6, previo7, previo8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_export_activity);

        binding = CustomExportActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //poblamos el set de spinners
        misSpinners.add(binding.spinnerCabys);
        misSpinners.add(binding.spinnerCodebar);
        misSpinners.add(binding.spinnerDesc);
        misSpinners.add(binding.spinnerIva);
        misSpinners.add(binding.spinnerCant);
        misSpinners.add(binding.spinnerBodega);
        misSpinners.add(binding.spinnerCompra);
        misSpinners.add(binding.spinnerVenta);

        //Inicializa los valores de los spinners
        Iterator<Spinner> recorreSpinners = misSpinners.iterator();

        Integer indexRecorre = 0;

        while (recorreSpinners.hasNext()) {
            Spinner miSpinner = recorreSpinners.next();
            Log.e("Tags",miSpinner.toString());
            miSpinner.setSelection(indexRecorre);
            indexRecorre++;
        }

        //Asigna los valores iniciales
        actual1 = 0;
        actual2 = 1;
        actual3 = 2;
        actual4 = 3;
        actual5 = 4;
        actual6 = 5;
        actual7 = 6;
        actual8 = 7;


        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);

        inicializaValores(sharedpreferences);

        binding.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                guardaValores(sharedpreferences);
            }
        });

        binding.spinnerCabys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                previo1 = actual1;
                actual1 = position;

                Log.e("Tags","SpinnerCabys: "+previo1+">"+actual1 );

                SeteaSpinners(actual1,  previo1,  0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerCodebar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo2 = actual2;
                actual2 = position;

                Log.e("Tags","SpinnerCodBar: "+previo2+">"+actual2 );

                SeteaSpinners(actual2,  previo2,  1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerDesc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo3 = actual3;
                actual3 = position;

                Log.e("Tags","SpinnerDesc: "+previo3+">"+actual3 );

                SeteaSpinners(actual3,  previo3,  2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerIva.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo4 = actual4;
                actual4 = position;

                Log.e("Tags","SpinnerIVA: "+previo4+">"+actual4 );

                SeteaSpinners(actual4,  previo4,  3);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerCant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo5 = actual5;
                actual5 = position;

                Log.e("Tags","SpinnerCantidad: "+previo5+">"+actual5 );

                SeteaSpinners(actual5,  previo5,  4);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerBodega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo6 = actual6;
                actual6 = position;

                Log.e("Tags","SpinnerBodega: "+previo6+">"+actual6 );

                SeteaSpinners(actual6,  previo6,  5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerCompra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo7 = actual7;
                actual7 = position;

                Log.e("Tags","SpinnerCompra: "+previo7+">"+actual7 );

                SeteaSpinners(actual7,  previo7,  6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spinnerVenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previo8= actual8;
                actual8 = position;

                Log.e("Tags","SpinnerVenta: "+previo8+">"+actual8 );

                SeteaSpinners(actual8,  previo8,  7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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


    public void SeteaSpinners(int nuevoValor, int viejoValor, int spinIndex) {

        Iterator<Spinner> recorreSpinners = misSpinners.iterator();

        Integer index = 0;

        while (recorreSpinners.hasNext()) {

            Spinner actual = recorreSpinners.next();

            if(actual.getSelectedItemPosition() == nuevoValor && index != spinIndex ) {

                actual.setSelection(viejoValor);

            }
            index ++;

        }



    }


}
