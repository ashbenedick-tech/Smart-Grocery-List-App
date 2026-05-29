package com.company.smartgrocerylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartGroceryList.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LISTS = "grocery_lists";
    public static final String COLUMN_LIST_ID = "list_id";
    public static final String COLUMN_DATE_CREATED = "date_created";

    public static final String TABLE_ITEMS = "grocery_items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_LIST_ID = "list_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_QUANTITY = "quantity";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createListsTable = "CREATE TABLE " + TABLE_LISTS + " ("
                + COLUMN_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE_CREATED + " TEXT)";

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " ("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ITEM_LIST_ID + " INTEGER, "
                + COLUMN_ITEM_NAME + " TEXT, "
                + COLUMN_QUANTITY + " TEXT)";

        db.execSQL(createListsTable);
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }

    public long addGroceryList(String dateCreated) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_CREATED, dateCreated);

        return db.insert(TABLE_LISTS, null, values);
    }

    public long addGroceryItem(long listId, String itemName, String quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_LIST_ID, listId);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);

        return db.insert(TABLE_ITEMS, null, values);
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LISTS + " ORDER BY " + COLUMN_DATE_CREATED + " DESC", null);
    }

    public Cursor getItemsForList(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_LIST_ID + " = ?",
                new String[]{String.valueOf(listId)}
        );
    }

    public Cursor getListsByMonthYear(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] parts = monthYear.split("/");

        if (parts.length != 2) {
            return db.rawQuery("SELECT * FROM " + TABLE_LISTS + " WHERE 1=0", null);
        }

        String month = parts[0];
        String year = parts[1];

        return db.rawQuery(
                "SELECT * FROM " + TABLE_LISTS + " WHERE " + COLUMN_DATE_CREATED + " LIKE ? ORDER BY " + COLUMN_DATE_CREATED + " DESC",
                new String[]{month + "/%/" + year}
        );
    }

    public String getListDateById(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_DATE_CREATED + " FROM " + TABLE_LISTS + " WHERE " + COLUMN_LIST_ID + " = ?",
                new String[]{String.valueOf(listId)}
        );

        String dateCreated = "";

        try {
            if (cursor.moveToFirst()) {
                dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_CREATED));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return dateCreated;
    }

    public Cursor getMonthlyFavoriteItems(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] parts = monthYear.split("/");

        if (parts.length != 2) {
            return db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE 1=0", null);
        }

        String month = parts[0];
        String year = parts[1];

        return db.rawQuery(
                "SELECT i." + COLUMN_ITEM_NAME + ", COUNT(*) AS item_count " +
                        "FROM " + TABLE_ITEMS + " i " +
                        "INNER JOIN " + TABLE_LISTS + " l ON i." + COLUMN_ITEM_LIST_ID + " = l." + COLUMN_LIST_ID + " " +
                        "WHERE l." + COLUMN_DATE_CREATED + " LIKE ? " +
                        "GROUP BY i." + COLUMN_ITEM_NAME + " " +
                        "ORDER BY item_count DESC, i." + COLUMN_ITEM_NAME + " ASC " +
                        "LIMIT 10",
                new String[]{month + "/%/" + year}
        );
    }



}
