package com.myapps.mobilecheck.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dhong on 12/11/16.
 */

public class CountDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String COUNT_TABLE_NAME = "count";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COUNT = "count";
    private static final String CREATE_COUNT_TABLE = "CREATE TABLE " + COUNT_TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE + " TEXT, "
            + COLUMN_COUNT + " INTEGER DEFAULT 0" + ")";

    public CountDatabase(Context context) {
        super(context, COUNT_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COUNT_TABLE_NAME);
        onCreate(db);
    }
}
