package com.example.scanapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.scanapp.databinding.MainActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;
    private FirebaseAuth mAuth;
    public String scanResult;
    public static String CustomDescription;

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

        //Creamos el Alert Dialog que permite limpiar la lista de productos
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirma Limpiar");
        builder.setMessage("Se Eliminaran todos los elementos");
        builder.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        db.execSQL("DELETE FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS) ;

                        RenderStringView ();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

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
                    case R.id.export:
                        Exporta();
                        break;
                    case  R.id.limpiar:
                        AlertDialog dialog = builder.create();
                        dialog.show();

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
    protected void onStart() {
        super.onStart();
        FirebaseUser ActiveUser = mAuth.getCurrentUser();

        if(ActiveUser == null) {
            Intent myIntent = new Intent(MainActivity.this,LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Inicializamos resultado pero solo si recibo data desde la activity EDIT verificando el EDIT_ACTIVITY_REQUEST_CODE que le definimos

        Log.i("TAG", "onActivityResult: "+resultCode);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);
        //Toma en contenido de el intent y lo pone en el edittext
        if (requestCode != 1 && requestCode != 2 ) { // Verifica q sea un result de la camara
            if (intentResult.getContents() != null) {

                Log.i("TAG", "onActivityResult: Camara");

                scanResult = intentResult.getContents();

                if (ExisteCodbarLocal(scanResult)) {
                    AgregaArticuloDesdeBdLocal(scanResult);
                } else {
                    BuscaArticulo(scanResult);
                }

                // Muestra El contenido, en este caso CODEBAR
                //Toast.makeText(this, intentResult.getContents(), Toast.LENGTH_SHORT).show();

                if (sharedpreferences.getString("CHECK_CONTINUO", "1").equals("1")) {
                    binding.btnScan.performClick();
                }

            } else {
                //Cuando el contenido del resultado es null
                Toast.makeText(getApplicationContext(), "Error, contenido nulo", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DESCRIPTION_SELECT_REQUEST_CODE) {
            Log.i("TAG", "onActivityResult: CustomDescription");
            if(resultCode == Activity.RESULT_OK) {

                String MyCodbar = data.getStringExtra("codbar");
                String MyDescription = data.getStringExtra("descripcion");
                Log.i("TAG", "MyCodbar: " + MyCodbar + "MyDescription: " + MyDescription);


                String MySql = "UPDATE tb_articulos set ARTICULO =\"" + MyDescription + "\" WHERE CODBAR=" + MyCodbar;

                Log.i("TAG", "MySQL: " + MySql);

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.execSQL(MySql);

                /*ContentValues values = new ContentValues();

                values.put(FeedReaderContract.FeedEntry.COLUMN_ARTICULO, MyDescription);

                //Ejecuta el ingreso a la BD
                long newRowId = db.update(FeedReaderContract.FeedEntry.TABLE_NAME, values , "CODBAR = ?", new String[]{MyCodbar});*/

                //finish();
                //CustomDescription=data.getStringExtra("descripcion");
                Log.i("TAG", "onActivityResult: Variable CUSTOMDESCRIPTION - "+MyDescription);
                Log.i("TAG", "onActivityResult: RESULT OK - "+data.getStringExtra("descripcion"));

            }
        }
        RenderStringView ();

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

                        //Variable dende guardaremos la descripción
                        String MiDescripcion;

                        //Agrega a datos la respuesta
                        datos.add(response.toString());

                        //Agregamos a la BD
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        try {
                            //Guarda el response en un jsonobject
                            JSONObject objResponse = new JSONObject(response.toString());
                            ContentValues values_articulos = new ContentValues();
                            ContentValues values_bd_local = new ContentValues();


                            if (objResponse.getString("CABYS").toString() != "ERROR") {
                                //Chequea si existe ya el cabys  en la BD (y el checkbox esta chequeado), Si es así updatea la cantidad
                                if (ExisteCabys(objResponse.getString("CABYS")) && checkCheckBoxStatus("sumariza")) {

                                    String dbQuery = "UPDATE " + FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS + " SET CANTIDAD = CANTIDAD +1 WHERE CABYS=\"" + objResponse.getString("CABYS") + "\"";
                                    db.execSQL(dbQuery);

                                } else {
                                    //Genera los values que serán ingresados a la BD y parsea el contenido del jsonObject en los campos

                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CABYS, objResponse.getString("CABYS"));

                                    //Reemplazamos el object response descripcion por el string generado
                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_ARTICULO, objResponse.getString("DESCRIPCION"));

                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_IVA, objResponse.getString("IMPUESTO"));
                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CODBAR, objResponse.getString("CODBAR"));

                                    //Si actualiza Inventario está on
                                    if (checkCheckBoxStatus("sumariza")) {
                                        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "1");
                                    } else {
                                        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "0");
                                    }
                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_BODEGA, "0");
                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_COMPRA, "0");
                                    values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_VENTA, "0");

                                    //Generamos entry en BD local
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_CABYS, objResponse.getString("CABYS"));
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_ARTICULO, objResponse.getString("DESCRIPCION"));
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_IVA, objResponse.getString("IMPUESTO"));
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_CODBAR, objResponse.getString("CODBAR"));
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "0");
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_BODEGA, "0");
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_COMPRA, "0");
                                    values_bd_local.put(FeedReaderContract.FeedEntry.COLUMN_VENTA, "0");
                                    //


                                    //Ejecuta el ingreso a la BD
                                    //long newRowId =
                                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS, null, values_articulos);
                                    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_BD_LOCAL, null, values_bd_local);
                                }
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                        //Si el array de descripciones contiene mas de 1 elemento muestra Custom Description, si no carga el


                        if(GetDescripcion(codigo).length>1) {
                            Intent customDescription = new Intent(MainActivity.this,CustomDescription.class);
                            customDescription.putExtra("DESCRIPCIONES",GetDescripcion(codigo));
                            customDescription.putExtra("CODBAR",codigo);

                            startActivityForResult(customDescription,DESCRIPTION_SELECT_REQUEST_CODE);
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

    public String[] GetDescripcion(String codbar) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String dbQuery = "SELECT ARTICULO FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS +" WHERE codbar=" + codbar;

        Cursor cursor = db.rawQuery(dbQuery, null);
        cursor.moveToFirst();

        String descripcion;
        descripcion = cursor.getString(cursor.getColumnIndex("ARTICULO"));

        Toast.makeText(this, descripcion, Toast.LENGTH_SHORT).show();


        return descripcion.replaceFirst("-"," ").split("-");

    }

    public boolean ExisteCabys(String cabys) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String dbQuery = "SELECT * FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS +" WHERE CABYS=" + cabys;

        Cursor cursor = db.rawQuery(dbQuery, null);


        return !(cursor.getCount()<=0);

    }

    public boolean ExisteCodbarLocal(String codbar) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String dbQuery = "SELECT * FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_BD_LOCAL +" WHERE CODBAR=" + codbar;

        Cursor cursor = db.rawQuery(dbQuery, null);


        return !(cursor.getCount()<=0);

    }

    public void AgregaArticuloDesdeBdLocal(String codbar) {

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values_articulos = new ContentValues();

        String dbQuery = "SELECT * FROM "+FeedReaderContract.FeedEntry.TABLE_NAME_BD_LOCAL +" WHERE CODBAR=" + codbar;

        Cursor cursor = db.rawQuery(dbQuery, null);

        cursor.getString(3);

        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_ARTICULO, cursor.getString(0));
        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CABYS, cursor.getString(1));
        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_IVA, cursor.getString(2));

        //Si actualiza Inventario está on
        if (checkCheckBoxStatus("sumariza")) {
            values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "1");
        } else {
            values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CANTIDAD, "0");
        }

        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_CODBAR, cursor.getString(4));
        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_BODEGA, cursor.getString(5));
        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_COMPRA, cursor.getString(6));
        values_articulos.put(FeedReaderContract.FeedEntry.COLUMN_VENTA, cursor.getString(7));

        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS, null, values_articulos);

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
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS;
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
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS;

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

    public void Exporta () {
        //Define el path donde van a quedar los archivos
        //String path = Environment.getExternalStorageDirectory() + "/cabys";

        SharedPreferences sharedpreferences;
        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);
        String path = null;
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        //Asigna las posiciones segun el orden establecido en los sharedpreferences y según el tipo de expo
        String estadoOrdenListado = sharedpreferences.getString("RADIO_GROUP_SISTEMA","0");
        String estadoMetodoDescarga = sharedpreferences.getString("RADIO_GROUP_TIPO","0");

        //Toma el nombre de archivo
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String nombreDeArchivo = "Export-"+date;

        String cadenaExporta = "";


        //Verifica si
        if (estadoMetodoDescarga.equals("0")) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        } else if (estadoMetodoDescarga.equals("1")) {                           //if (sharedpreferences.getString("METODODESCARGA","1") == "1")
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        };

        /*else {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            //Toast.makeText(this, "El path 1: "+path, Toast.LENGTH_SHORT).show();
        };*/



        //Crea dir para crear el directorio del path (cabys)
        File dir = new File(path);
        dir.mkdirs();
        //Crea el archivo donde se guardan los datos

        File myCVS = new File(path + "/"+nombreDeArchivo+".csv");

        //ArrayList que va a popular el ListView
        ArrayList<String> arrayDeArticulos = new ArrayList<String>();

        //Conectamos a la BD
        String dbQuery = "SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME_ARTICULOS;
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Cursor que va a recorrer la BD
        Cursor cursor = db.rawQuery(dbQuery, null);

        // Genera los headers
        if(estadoOrdenListado.equals("0")){ arrayDeArticulos.add("COD.CABYS,COD.PRODUCTO,NOMBRE PROD.,ES SERVICIO,COD.DEPARTAMENTO,COD.ARTICULO,NOMBRE ART.,GRAVADO,IV PORCENTAJE,COD.IMPTO 2,IMPUESTO 2,COD.PROVEEDOR,COD.BODEGA,EXISTENCIAS,BASE CALCULO,COEFICIENTE 1(%),COEFICIENTE 2(%),REDONDEO,SUMA FIJA,PRECIO VENTA,MAX.DESCUENTO,ACTUALIZA EXISTENCIA,VENTA AL PESO,PRECIO COMPRA,COD.ARTICULO2,COD.ARTICULO3,COD.ARTICULO4,UD.MEDIDA,N° SERIE,PRECIO MANUAL SIN IMPUESTO INLCUIDO,FAMILIA TALLA,TALLA,FAMILIA COLOR,COLOR,MARCA\n"); }
        else if(estadoOrdenListado.equals("1")){ arrayDeArticulos.add("NOMBRE (CAMPO OBLIGATORIO),CODIGO (CAMPO OBLIGATORIO),¿ES SERVICIO?,CODIGO PROVEEDOR,EXISTENCIAS,% DESCUENTO MAXIMO,PRECIO DE VENTA CON IVA,PORCENTAJE DE IVA,¿ES GRAVADO?,PRECIO DE COMPRA SIN IVA\n"); }
        else { cadenaExporta = ""; }

        //Cargamos el ArrayList
        if (cursor.isBeforeFirst()) {
            while (cursor.moveToNext()) {

                if(estadoOrdenListado.equals("0")){ //Export PDV
                    // Es esta lista se guardan los elementos que se exportaran
                    String[] registro ={"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};

                    registro[2] = cursor.getString(0).replaceAll("[^a-zA-Z0-9]"," "); //Descripcion
                    registro[0] = cursor.getString(1);  //CABYS
                    registro[8] = cursor.getString(2);  //IVA
                    registro[21] = cursor.getString(3); //CANTIDAD
                    registro[1] = cursor.getString(4);  //CODBAR

                    cadenaExporta = "";
                    for (String s : registro) {
                        cadenaExporta += s + ",";
                    }
                    cadenaExporta += "\n";
                    arrayDeArticulos.add(cadenaExporta);

                } else if(estadoOrdenListado.equals("1")){ // Export Avanza
                    // Es esta lista se guardan los elementos que se exporaran
                    String[] registro ={"","","","","","","","","",""};

                    registro[2] = cursor.getString(0).replaceAll("[^a-zA-Z0-9]"," "); //Descripcion
                    registro[0] = cursor.getString(1);  //CABYS
                    registro[7] = cursor.getString(2);  //IVA
                    registro[4] = cursor.getString(3); //CANTIDAD
                    registro[1] = cursor.getString(4);  //CODBAR

                    cadenaExporta = "";
                    for (String s : registro) {
                        cadenaExporta += s + ",";
                    }
                    cadenaExporta += "\n";
                    arrayDeArticulos.add(cadenaExporta);

                } else { // Export personalizado

                    //Se genera un registro con los campos de exportación requeridos, luego son llenados
                    String[] registro ={"","","","","","","",""};

                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERDESC","0"))] = cursor.getString(0).replaceAll("[^a-zA-Z0-9]"," ");
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERCABYS","1"))] = cursor.getString(1);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERIVA","2"))] = cursor.getString(2);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERCANT","3"))] = cursor.getString(3);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERCODBAR","4"))] = cursor.getString(4);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERBODEGA","5"))] = cursor.getString(5);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERCOMPRA","6"))] = cursor.getString(6);
                    registro[Integer.parseInt(sharedpreferences.getString("SPINNERVENTA","7"))] = cursor.getString(7);

                    arrayDeArticulos.add(registro[0]+","+registro[1]+","+registro[2]+","+registro[3]+","+registro[4]+","+registro[5]+","+registro[6]+","+registro[7]+"\n");

//                        arrayDeArticulos.add(cursor.getString(1)+
//                                ","+cursor.getString(0).replaceAll("[^a-zA-Z0-9]"," ")+
//                                ","+cursor.getString(2)+","+cursor.getString(3)+"\n");
                }
            }}

        Toast.makeText(this, (("file://"+myCVS)), Toast.LENGTH_SHORT).show();
        Save(myCVS, arrayDeArticulos, getApplicationContext() );

        if (estadoMetodoDescarga.equals("1")) {

            Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show();


            if(!myCVS.exists()) {
                Toast.makeText(this, "No file!", Toast.LENGTH_LONG).show();
                return;
            }

            //Identificador unico de recursos que define el path del archivo
            Uri myCVSpath = FileProvider.getUriForFile(this, "com.example.scanapp", myCVS);

            //Definimos un intent tipo ACTION_SEND de la intent Class
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Scan App Export");
            shareIntent.putExtra(Intent.EXTRA_STREAM, myCVSpath);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Enviar..."));

        }
    }


    //Guarda Archivo
    public static void Save(@NonNull File file, ArrayList<String> lista, Context context)  {

        Toast.makeText(context, file.getPath().toString(), Toast.LENGTH_SHORT).show();
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (String elem:lista)
                {
                    fos.write(elem.getBytes());
                }
                //fos.write("\n".getBytes());
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

}
