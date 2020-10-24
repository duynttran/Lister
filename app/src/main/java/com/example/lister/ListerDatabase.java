package com.example.lister;
import android.provider.BaseColumns;

public final class ListerDatabase {
    private ListerDatabase() {}

    /* Table for List*/
    public static class List implements BaseColumns {
        public static final String TABLE_NAME = "Lists";
        public static final String LIST_NAME = "listName";
    }

    /* Table for Item*/
    public static class Item implements BaseColumns {
        public static final String TABLE_NAME = "Items";
        public static final String ITEM_NAME = "itemName";
        public static final String ITEM_QUANTITY = "itemQuantity";
        public static final String ITEM_PRICE = "itemPrice";
        public static final String LIST_ID_FK = "listID";
    }
}

