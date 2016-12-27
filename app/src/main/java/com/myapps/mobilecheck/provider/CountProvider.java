package com.myapps.mobilecheck.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.myapps.mobilecheck.database.CountDatabase;

/**
 * Created by dhong on 12/11/16.
 */

public class CountProvider extends ContentProvider {

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.myapps.mobilecheck.count.provider";
    private static final int CODE_COUNT = 1;

    private CountDatabase countDatabaseHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, CountDatabase.COUNT_TABLE_NAME, CODE_COUNT);
    }

    public static Uri getUri() {
        return Uri.parse(SCHEME + AUTHORITY + "/" + CountDatabase.COUNT_TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        countDatabaseHelper = new CountDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case CODE_COUNT:
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        SQLiteDatabase db = countDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query(CountDatabase.COUNT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_COUNT:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.mobilecheck.count";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_COUNT:
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        SQLiteDatabase db = countDatabaseHelper.getWritableDatabase();
        long id = db.insert(CountDatabase.COUNT_TABLE_NAME, null, values);
        if (id > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLiteException("Problem with inserting into uri: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_COUNT:
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        SQLiteDatabase db = countDatabaseHelper.getWritableDatabase();
        int updatedRows = db.update(CountDatabase.COUNT_TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

}
