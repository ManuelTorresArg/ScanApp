package com.example.scanapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // Cuando cambiamos la estructura de la bd debemos cambiar la versión
    // Para que se ejecuten en forma automatica los metodos de onUpgrade o onDowngrade
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "stock.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creación de la BD según lo establecido en el contrato
    public void onCreate(SQLiteDatabase db) {
        //Crea tabla articulos
        db.execSQL(FeedReaderContract.SQL_CREATE_ENTRIES);
        //Crea tabla session (No en uso)
        //db.execSQL(FeedReaderContract.SQL_CREATE_SESSION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // El unico sentido de esta bd es mantener temporalmente datos, asi que cuando
        // cambia la versión se borra y es recreada
        db.execSQL(FeedReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}