package com.example.lister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
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
            ListerDatabase.List._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            ListerDatabase.List.LIST_NAME + " TEXT)";
        String SQL_CREATE_ITEMS = "CREATE TABLE " + ListerDatabase.Item.TABLE_NAME + " (" +
            ListerDatabase.Item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
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

    public void addList(String list) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(ListerDatabase.List.LIST_NAME, list);
            db.insertOrThrow(ListerDatabase.List.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Unable to add list to database");
        } finally {
            db.endTransaction();
        }
    }

    public List<AbstractMap.SimpleEntry<String, Integer>> getAllLists() {
        SQLiteDatabase db = getReadableDatabase();
        List<AbstractMap.SimpleEntry<String, Integer>> lists = new ArrayList<>();
        String LIST_QUERY = String.format("SELECT * FROM %s", ListerDatabase.List.TABLE_NAME);
        Cursor cursor = db.rawQuery(LIST_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String list = cursor.getString(cursor.getColumnIndex(ListerDatabase.List.LIST_NAME));
                    int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ListerDatabase.List._ID)));
                    lists.add(new AbstractMap.SimpleEntry<String, Integer>(list, id));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Database", "Unable to retrieve lists from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return lists;
    }

    public int updateListName(String list, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListerDatabase.List.LIST_NAME, list);
        return db.update(ListerDatabase.List.TABLE_NAME, values,
                ListerDatabase.List._ID + "= " + id, null);
    }

    public void deleteList(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(ListerDatabase.List.TABLE_NAME, ListerDatabase.List._ID + "=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Lifecycle", "Unable to delete all lists");
        } finally {
            db.endTransaction();
        }
    }
}
