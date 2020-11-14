package com.example.lister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ListDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lister5.db";
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
        String SQL_CREATE_LISTS = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,%s TEXT)",
                ListerDatabase.List.TABLE_NAME,
                ListerDatabase.List._ID,
                ListerDatabase.List.LIST_NAME);
        String SQL_CREATE_ITEMS = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,%s TEXT,%s INTEGER,%s FLOAT,%s INTEGER REFERENCES %s)",
                ListerDatabase.Item.TABLE_NAME,
                ListerDatabase.Item._ID,
                ListerDatabase.Item.ITEM_NAME,
                ListerDatabase.Item.ITEM_QUANTITY,
                ListerDatabase.Item.ITEM_PRICE,
                ListerDatabase.Item.LIST_ID_FK,
                ListerDatabase.List.TABLE_NAME);
        db.execSQL(SQL_CREATE_LISTS);
        db.execSQL(SQL_CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", ListerDatabase.List.TABLE_NAME));
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", ListerDatabase.Item.TABLE_NAME));
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
                    lists.add(new AbstractMap.SimpleEntry<>(list, id));
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

    public void updateListName(String list, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListerDatabase.List.LIST_NAME, list);
        db.update(ListerDatabase.List.TABLE_NAME, values,
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

    public String getListName(int id){
        SQLiteDatabase db = getReadableDatabase();
        String listQuery = String.format("SELECT * FROM %s WHERE _ID=%s", ListerDatabase.List.TABLE_NAME, id);
        Cursor cursor = db.rawQuery(listQuery, null);
        String listName = "";
        try {
            if (cursor.moveToFirst()) {
                listName = cursor.getString(cursor.getColumnIndex(ListerDatabase.List.LIST_NAME));
            }
        } catch (Exception e) {
            Log.d("Database", "Unable to retrieve lists from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return listName;
    }

    public int addItem(ListItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int itemId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(ListerDatabase.Item.LIST_ID_FK, Integer.toString(item.getListId()));
            values.put(ListerDatabase.Item.ITEM_QUANTITY, Integer.toString(item.getQuantity()));
            values.put(ListerDatabase.Item.ITEM_NAME, item.getName());
            values.put(ListerDatabase.Item.ITEM_PRICE, Double.toString(item.getPrice()));
            itemId = (int) db.insert(ListerDatabase.Item.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Database", "Unable to add item to database");
        } finally {
            db.endTransaction();
        }
        return itemId;
    }

    public List<ListItem> getListItems(int id){
        SQLiteDatabase db = getReadableDatabase();
        List<ListItem> items = new ArrayList<>();
        String LIST_QUERY = String.format("SELECT * FROM %s WHERE %s=%s", ListerDatabase.Item.TABLE_NAME, ListerDatabase.Item.LIST_ID_FK,id);
        Cursor cursor = db.rawQuery(LIST_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String itemName = cursor.getString(cursor.getColumnIndex(ListerDatabase.Item.ITEM_NAME));
                    int itemQuantity = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ListerDatabase.Item.ITEM_QUANTITY)));
                    double itemPrice = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ListerDatabase.Item.ITEM_PRICE)));
                    int listId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ListerDatabase.Item.LIST_ID_FK)));
                    int itemId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ListerDatabase.Item._ID)));
                    ListItem item = new ListItem(itemName, itemQuantity, itemPrice, listId);
                    item.setItemId(itemId);
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Database", "Unable to retrieve lists from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return items;
    }

    public void updateItemName(String itemName, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListerDatabase.Item.ITEM_NAME, itemName);
        db.update(ListerDatabase.Item.TABLE_NAME, values,
                ListerDatabase.Item._ID + "= " + id, null);
    }

    public void updateItemQuantity(int itemCount, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListerDatabase.Item.ITEM_QUANTITY, Integer.toString(itemCount));
        db.update(ListerDatabase.Item.TABLE_NAME, values,
                ListerDatabase.Item._ID + "= " + id, null);
    }

    public void updateItemPrice(double itemPrice, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ListerDatabase.Item.ITEM_PRICE, Double.toString(itemPrice));
        db.update(ListerDatabase.Item.TABLE_NAME, values,
                ListerDatabase.Item._ID + "= " + id, null);
    }

    public String getItemPrice(int id){
        SQLiteDatabase db = getReadableDatabase();
        String LIST_QUERY = String.format("SELECT itemPRICE FROM %s WHERE %s=%s", ListerDatabase.Item.TABLE_NAME, ListerDatabase.Item._ID,id);
        Cursor cursor = db.rawQuery(LIST_QUERY, null);
        String price = "";
        try {
            if (cursor.moveToFirst()) {
                price = cursor.getString(cursor.getColumnIndex(ListerDatabase.Item.ITEM_PRICE));
            }
        } catch (Exception e) {
            Log.d("Database", "Unable to retrieve item price from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return price;
    }

    public void deleteItem(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(ListerDatabase.Item.TABLE_NAME, ListerDatabase.Item._ID + "=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Lifecycle", "Unable to delete item");
        } finally {
            db.endTransaction();
        }
    }
}
