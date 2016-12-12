package com.myapps.mobilecheck.activity;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.myapps.mobilecheck.R;
import com.myapps.mobilecheck.database.CountDatabase;
import com.myapps.mobilecheck.provider.CountProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_COUNT = "count";

    private CountObserver countObserver;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(KEY_COUNT);
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_COUNT, count);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countObserver = new CountObserver(new Handler());
        getContentResolver().registerContentObserver(CountProvider.getUri(), true, countObserver);
        if (count == 0) {
            fetchCount();
        } else {
            showText(count);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countObserver != null) {
            getContentResolver().unregisterContentObserver(countObserver);
        }
    }

    private void showText(int count) {
        TextView text = (TextView) findViewById(R.id.main_text_view);
        text.setText(String.format(getString(R.string.main_text), count));
    }

    private void fetchCount() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                return getCountOfCurrentDate();
            }

            @Override
            protected void onPostExecute(Integer count) {
                MainActivity.this.count = count;
                showText(count);
            }
        }.execute();
    }

    /**
     * should be called from a worker thread
     * @return count of current date, 0 if it does not exist in db
     */
    private int getCountOfCurrentDate() {
        int count = 0;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String[] countColumn = new String[] { CountDatabase.COLUMN_COUNT };
        String selection = CountDatabase.COLUMN_DATE + "=?";
        String[] selectionArgs = new String[] { currentDate };
        Cursor c = getContentResolver().query(CountProvider.getUri(), countColumn, selection, selectionArgs, null);
        if (c == null) return count;
        try {
            if (c.moveToFirst()) {
                count = c.getInt(c.getColumnIndex(CountDatabase.COLUMN_COUNT));
            }
        } finally {
            c.close();
        }
        return count;
    }


    private class CountObserver extends ContentObserver {

        public CountObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            fetchCount();
        }
    }
}
