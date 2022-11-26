package com.example.scanapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scanapp.databinding.MainActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;
    private FirebaseAuth mAuth;
    public String scanResult;

    //Creamos el request code del activity edit
    private static final int EDIT_ACTIVITY_REQUEST_CODE = 1;
    private static final int DESCRIPTION_SELECT_REQUEST_CODE = 2;
    private static final int SCAN_ACTIVITY_REQUEST_CODE = 3;

    //En datos voy a guardar los elementos que reciba
    ArrayList<String> datos = new ArrayList<String>();

    // Creamos conexión BD
    FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);

    //Con sharedpreferences vamos a leer/modificar los valores predeterminados del usuario
    public SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.main_activity);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Carga ListView si hay datos guardados
        RenderStringView();

        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);


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
                    case R.id.salir:
                        mAuth.signOut();
                        finish();
                        break;

                }


                return false;
            }
        });

        binding.lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                JSONObject datosEditar = onGetIndex (i, MainActivity.this );

                Log.i("TAG", "onItemClick: "+datosEditar.toString());

                Intent editActivity = new Intent(MainActivity.this, EditActivity.class);

                try {
                    editActivity.putExtra("ARTICULO",datosEditar.getString("ARTICULO"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("CABYS",datosEditar.getString("CABYS"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("IVA",datosEditar.getString("IVA"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("CODBAR",datosEditar.getString("CODBAR"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("CANTIDAD",datosEditar.getString("CANTIDAD"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("BODEGA", datosEditar.getString("BODEGA"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("PCOMPRA", datosEditar.getString("PCOMPRA"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    editActivity.putExtra("PVENTA", datosEditar.getString("PVENTA"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivityForResult(editActivity, EDIT_ACTIVITY_REQUEST_CODE);

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
        if (requestCode != 1 && requestCode != 2 ) { // Verifica q sea un result de la camara
            if (intentResult.getContents() != null) {

                scanResult = intentResult.getContents();
                BuscaArticulo(scanResult);

                Toast.makeText(this, intentResult.getContents(), Toast.LENGTH_SHORT).show();

            } else {
                //Cuando el contenido del resultado es null
                Toast.makeText(getApplicationContext(), "Error, contenido nulo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void BuscaArticulo(String codigo) {

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String url = "";

        url = "https://scanapp.onrender.com/cabys/" + codigo.toString().trim();
        Log.i("URL", "BuscaArticulo: "+url);

        Toast.makeText(MainActivity.this, codigo+" : " + url , Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Muestra los primeros 500 caracteres de la respuesta
                        Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);

                        //Agrega a datos la respuesta
                        datos.add(response.toString());

                        //Agregamos a la BD
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        try {
                            //Guarda el response en un jsonobject
                            JSONObject objResponse = new JSONObject(response.toString());
                            ContentValues values = new ContentValues();

                            String[] descriptionsArray = objResponse.getString("DESCRIPCION").replaceFirst("-"," ").split("-");

                            Log.i("TAG", "onResponse: "+objResponse.getString("DESCRIPCION"));

                            //Si el array de descripciones contiene mas de 1 elemento muestra Custom Description
                           if(descriptionsArray.length > 1) {
                                Intent myIntent = new Intent(MainActivity.this,CustomDescription.class);
                                myIntent.putExtra("DESCRIPCIONES",descriptionsArray);
                                MainActivity.this.startActivity(myIntent);
                            }

                            if (objResponse.getString("CABYS").toString() != "ERROR") {
                                //Chequea si existe ya el cabys  en la BD (y el checkbox esta chequeado), Si es así updatea la cantidad
                                if (ExisteCabys(objResponse.getString("CABYS")) && checkCheckBoxStatus("sumariza")) {

                                    String dbQuery = "UPDATE " + FeedReaderContract.FeedEntry.TABLE_NAME + " SET CANTIDAD = CANTIDAD +1 WHERE CABYS=\"" + objResponse.getString("CABYS") + "\"";
                                    db.execSQL(dbQuery);

                                } else {
                                    //Genera los values que serán ingresados a la BD y parsea el contenido del jsonObject en los campos

                                    values.put(FeedReaderContract.FeedEntry.COLUMN_CABYS, objResponse.getString("CABYS"));
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_ARTICULO, objResponse.getString("DESCRIPCION"));
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_IVA, objResponse.getString("IMPUESTO"));
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_CODBAR, objResponse.getString("CODBAR"));

                                    //Si actualiza Inventario está on
                                    if (checkCheckBoxStatus("sumariza")) {
                                        values.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "1");
                                    } else {
                                        values.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "0");
                                    }
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_BODEGA, "0");
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_COMPRA, "0");
                                    values.put(FeedReaderContract.FeedEntry.COLUMN_VENTA, "0");

                                    //Ejecuta el ingreso a la BD
                                    //long newRowId =
                                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
                                }
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                        RenderStringView ();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error al conectar server: "+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public boolean ExisteCabys(String cabys) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String dbQuery = "SELECT * FROM "+FeedReaderContract.FeedEntry.TABLE_NAME+" WHERE CABYS=" + cabys;

        Cursor cursor = db.rawQuery(dbQuery, null);


        return !(cursor.getCount()<=0);

    }

    public boolean checkCheckBoxStatus(String nombreCheck){

        //Usamos esta funcion para evaluar el status de valores guardados en el sharepreferences

        switch (nombreCheck) {
            case "sumariza":
                return sharedpreferences.getString("CHECK_SUMARIZA", "1").equals("1");
            case "continuo":
                return sharedpreferences.getString("CHECK_CONTINUO", "1").equals("1");
        }
        return false;
    }

    public void RenderStringView () {
        //ArrayList que va a popular el ListView
        //ArrayList <String> arrayDeArticulos = new ArrayList<String>();

        //Conectamos a la BD
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Para poder utilizar el miltiple line de Listview utilizamos un array de objetos
        ArrayList<Map<String, Object>> listaArticulos = new ArrayList<>();

        //Cursor que va a recorrer la BD
        Cursor cursor = db.rawQuery(dbQuery, null);

        //Cargamos el ArrayList

        if (cursor.moveToFirst()) {

            do {

                //Internamente creamos un amp (key - value) y lo agregamos al array
                Map<String,Object> articulo = new HashMap<>();
                articulo.put("Articulo",cursor.getString(0));
                articulo.put("Desc", cursor.getString(1)+"- Cant: "+cursor.getString(3));
                listaArticulos.add(articulo);

            } while (cursor.moveToNext()) ;
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listaArticulos, android.R.layout.simple_list_item_2, new String[]{"Articulo","Desc"},
                new int[]{android.R.id.text1,android.R.id.text2});

        binding.lvDatos.setAdapter(simpleAdapter);


    }

    private SimpleAdapter getAdapterListViewNombreCorreo(ArrayList<Map<String, Object>> listaUsuarios){
        return new SimpleAdapter(this, listaUsuarios,
                android.R.layout.simple_list_item_2, new String[]{"Nombre", "Correo"},
                new int[]{android.R.id.text1, android.R.id.text2}){

            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView txtNombre = view.findViewById(android.R.id.text1);

                TextView txtCorreo = view.findViewById(android.R.id.text2);

                return view;
            }

        };
    }

    public static JSONObject onGetIndex(int index, Context context) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        JSONObject miJson = new JSONObject();
        //Conectamos a la BD
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME;

        //ArrayList que vamos a crear para buscar
        ArrayList<String> arrayDeArticulos = new ArrayList<String>();

        //Cursor que va a recorrer la BD
        Cursor cursor = db.rawQuery(dbQuery, null);

        //Cargamos el ArrayList
        cursor.moveToPosition(index);

        String texto = String.valueOf(index);

        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();

        try {
            miJson.put("ARTICULO",cursor.getString(0))
                    .put("CABYS",cursor.getString(1))
                    .put("IVA",cursor.getString(2))
                    .put("CANTIDAD",cursor.getString(3))
                    .put("CODBAR",cursor.getString(4))
                    .put("BODEGA",cursor.getString(5))
                    .put("PCOMPRA",cursor.getString(6))
                    .put("PVENTA",cursor.getString(7));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return miJson;

    }



}
