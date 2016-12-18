package com.myapps.mobilecheck.activity;

import android.content.res.Resources;
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

    private CountObserver countObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        TextView topText = (TextView) findViewById(R.id.top_text);
        if (isTablet) {
            topText.setText(R.string.main_text_top_tablet);
        } else {
            topText.setText(R.string.main_text_top_phone);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        countObserver = new CountObserver(new Handler());
        getContentResolver().registerContentObserver(CountProvider.getUri(), true, countObserver);
        fetchCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countObserver != null) {
            getContentResolver().unregisterContentObserver(countObserver);
        }
    }

    private void showCount(int count) {
        TextView countText = (TextView) findViewById(R.id.main_text_count);
        countText.setText(String.valueOf(count));

        TextView bottomText = (TextView) findViewById(R.id.bottom_text);
        String bottomString = getResources().getQuantityString(R.plurals.main_text_bottom, count);
        bottomText.setText(bottomString);
    }

    private void fetchCount() {
        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... voids) {
                return getCountOfCurrentDate();
            }

            @Override
            protected void onPostExecute(Integer count) {
                showCount(count);
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
