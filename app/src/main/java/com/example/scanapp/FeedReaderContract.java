package com.example.scanapp;

import android.provider.BaseColumns;

// En esta clase se define el contrato que establece la forma de la base de datos
// Modificando la estructura desde esta clase permite controlar en forma centralizada

public final class FeedReaderContract {

    static final String SQL_CREATE_ENTRIES_ARTICULOS =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_ARTICULOS + " (" +
                    FeedEntry.COLUMN_ARTICULO + " TEXT," +
                    FeedEntry.COLUMN_CABYS + " TEXT," +
                    FeedEntry.COLUMN_IVA + " TEXT," +
                    FeedEntry.COLUMN_CANTIDAD + " TEXT," +
                    FeedEntry.COLUMN_CODBAR + " TEXT," +
                    FeedEntry.COLUMN_BODEGA + " TEXT," +
                    FeedEntry.COLUMN_COMPRA + " TEXT," +
                    FeedEntry.COLUMN_VENTA + " TEXT," +
                    "UNIQUE("+ FeedEntry.COLUMN_CABYS+"))";

    static final String SQL_CREATE_ENTRIES_BD_LOCAL =
            "CREATE TABLE " + FeedEntry.TABLE_NAME_BD_LOCAL + " (" +
                    FeedEntry.COLUMN_ARTICULO + " TEXT," +
                    FeedEntry.COLUMN_CABYS + " TEXT," +
                    FeedEntry.COLUMN_IVA + " TEXT," +
                    FeedEntry.COLUMN_CANTIDAD + " TEXT," +
                    FeedEntry.COLUMN_CODBAR + " TEXT," +
                    FeedEntry.COLUMN_BODEGA + " TEXT," +
                    FeedEntry.COLUMN_COMPRA + " TEXT," +
                    FeedEntry.COLUMN_VENTA + " TEXT," +
                    "UNIQUE("+ FeedEntry.COLUMN_CABYS+"))";


    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_ARTICULOS;

    private FeedReaderContract() {}

    /* Desde aqui controlamos el contenido de la tabla */
    public static class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME_ARTICULOS = "tb_articulos";
        public static final String TABLE_NAME_BD_LOCAL = "tb_dblocal";


        public static final String COLUMN_ARTICULO = "ARTICULO";
        public static final String COLUMN_CABYS = "CABYS";
        public static final String COLUMN_IVA = "IVA";
        public static final String COLUMN_CANTIDAD = "CANTIDAD";
        public static final String COLUMN_CODBAR = "CODBAR";
        public static final String COLUMN_BODEGA = "BODEGA";
        public static final String COLUMN_COMPRA = "COMPRA";
        public static final String COLUMN_VENTA = "VENTA";
    }


}