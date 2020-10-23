package com.example.lister;
import android.provider.BaseColumns;

public final class ListerDatabase {
    private ListerDatabase() {}

    /* Table for Lists*/
    public static class Lists implements BaseColumns {
        public static final String TABLE_NAME = "List";
    }

    /* Table for Items*/
    public static class Items implements BaseColumns {
        public static final String TABLE_NAME = "Item";
    }
}

