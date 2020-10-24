package com.example.lister;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ListDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lister.db";
    private static ListDatabaseHelper sInstance;

    public static synchronized ListDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ListDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ListDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_LISTS = "CREATE TABLE " + ListerDatabase.List.TABLE_NAME + " (" +
            ListerDatabase.List._ID + " INTEGER PRIMARY KEY NOT NULL," +
            ListerDatabase.List.LIST_NAME + " TEXT)";
        String SQL_CREATE_ITEMS = "CREATE TABLE " + ListerDatabase.Item.TABLE_NAME + " (" +
            ListerDatabase.Item._ID + " INTEGER PRIMARY KEY NOT NULL," +
            ListerDatabase.Item.ITEM_NAME + " TEXT," +
            ListerDatabase.Item.ITEM_QUANTITY + " INTEGER," +
            ListerDatabase.Item.ITEM_PRICE + " FLOAT," +
            ListerDatabase.Item.LIST_ID_FK + " INTEGER REFERENCES " +
            ListerDatabase.List.LIST_NAME + ")";
        db.execSQL(SQL_CREATE_LISTS);
        db.execSQL(SQL_CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + ListerDatabase.List.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ListerDatabase.Item.TABLE_NAME);
            onCreate(db);
        }
    }
}
