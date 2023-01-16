package com.example.scanapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.EditActivityBinding;


public class EditActivity extends AppCompatActivity {

    EditActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        binding = EditActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toast.makeText(this, getIntent().getStringExtra("ARTICULO"), Toast.LENGTH_SHORT).show();

        binding.edtArticulo.setText(getIntent().getStringExtra("ARTICULO").trim()) ;
        binding.edtCabys.setText(getIntent().getStringExtra("CABYS")) ;
        binding.edtImpuesto.setText(getIntent().getStringExtra("IVA")) ;
        binding.edtCantidad.setText(getIntent().getStringExtra("CANTIDAD")) ;
        binding.edtCodBar.setText(getIntent().getStringExtra("CODBAR"));
        binding.edtBodega.setText(getIntent().getStringExtra("BODEGA"));
        binding.edtPrecioCompra.setText(getIntent().getStringExtra("PCOMPRA"));
        binding.edtPrecioVenta.setText(getIntent().getStringExtra("PVENTA"));

        binding.seekBarCantidad.setProgress(Integer.parseInt(binding.edtCantidad.getText().toString()));

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(EditActivity.this);

        binding.btnEditBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.execSQL("DELETE FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS +" WHERE CABYS="+binding.edtCabys.getText().toString());

                finish();

            }
        });

        binding.btnEditAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, binding.edtCantidad.getText().toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_BODEGA, binding.edtBodega.getText().toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_IVA, binding.edtImpuesto.getText().toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_COMPRA, binding.edtPrecioCompra.getText().toString());
                values.put(FeedReaderContract.FeedEntry.COLUMN_VENTA, binding.edtPrecioVenta.getText().toString());

                //Ejecuta el ingreso a la BD
                long newRowId = db.update(FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS, values , "CABYS = ?", new String[]{binding.edtCabys.getText().toString()});

                finish();

            }
        });

        binding.btnEditCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }

        });

        binding.seekBarCantidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int valor, boolean b) {
                binding.edtCantidad.setText(String.valueOf(valor));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}
