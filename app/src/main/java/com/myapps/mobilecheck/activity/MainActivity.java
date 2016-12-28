package com.myapps.mobilecheck.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.myapps.mobilecheck.R;
import com.myapps.mobilecheck.database.CountDatabase;
import com.myapps.mobilecheck.provider.CountProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_CURRENT_DATE_COUNT = 0;

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
        getSupportLoaderManager().initLoader(LOADER_CURRENT_DATE_COUNT, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_history:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String[] countColumn = new String[] { CountDatabase.COLUMN_COUNT };
        String selection = CountDatabase.COLUMN_DATE + "=?";
        String[] selectionArgs = new String[] { currentDate };
        return new CursorLoader(this, CountProvider.getUri(), countColumn, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c == null) return;
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex(CountDatabase.COLUMN_COUNT));
        }
        showCount(count);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showCount(int count) {
        TextView countText = (TextView) findViewById(R.id.main_text_count);
        countText.setText(String.valueOf(count));

        TextView bottomText = (TextView) findViewById(R.id.bottom_text);
        String bottomString = getResources().getQuantityString(R.plurals.main_text_bottom, count);
        bottomText.setText(bottomString);
    }

}
