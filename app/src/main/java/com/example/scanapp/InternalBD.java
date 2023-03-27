package com.example.scanapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scanapp.databinding.InternalbdActivityBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalBD extends AppCompatActivity {

    InternalbdActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = InternalbdActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setContentView(R.layout.internalbd_activity);

        RenderStringView();

    }

    public void RenderStringView () {
        //ArrayList que va a popular el ListView
        //ArrayList <String> arrayDeArticulos = new ArrayList<String>();

        //Conectamos a la BD
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_BD_LOCAL;
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(InternalBD.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Para poder utilizar el miltiple line de Listview utilizamos un array de objetos
        ArrayList<Map<String, Object>> listaArticulos = new ArrayList<>();

        //Cursor que va a recorrer la BD
        Cursor cursor = db.rawQuery(dbQuery, null);

        //Cargamos el ArrayList

        if (cursor.moveToFirst()) {

            do {
                //Internamente creamos un map (key - value) y lo agregamos al array
                Map<String,Object> articulo = new HashMap<>();
                articulo.put("Articulo",cursor.getString(0));
                articulo.put("Desc", cursor.getString(4));
                listaArticulos.add(articulo);

            } while (cursor.moveToNext()) ;
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listaArticulos, R.layout.internaldb_row, new String[]{"Articulo","Desc"},
                new int[]{R.id.description,R.id.codbar});

        binding.lvDatosInternos.setAdapter(simpleAdapter);

    }

}
