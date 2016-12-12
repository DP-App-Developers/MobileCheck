package com.myapps.mobilecheck.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import com.myapps.mobilecheck.database.CountDatabase;
import com.myapps.mobilecheck.provider.CountProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dhong on 12/11/16.
 */

public class UserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new saveCountTask(context).execute();
    }

    private class saveCountTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public saveCountTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String[] countColumn = new String[] { CountDatabase.COLUMN_COUNT };
            String selection = CountDatabase.COLUMN_DATE + "=?";
            String[] selectionArgs = new String[] { currentDate };
            ContentValues values = new ContentValues();
            Cursor c = mContext.getContentResolver().query(CountProvider.getUri(), countColumn, selection, selectionArgs, null);
            if (c == null) return null;
            try {
                if (!c.moveToFirst()) {
                    // count not yet exists for current date, insert a count
                    values.put(CountDatabase.COLUMN_DATE, currentDate);
                    values.put(CountDatabase.COLUMN_COUNT, 1); // user have checked device once
                    mContext.getContentResolver().insert(CountProvider.getUri(), values);
                } else {
                    // increment existing count by 1
                    int oldCount = c.getInt(c.getColumnIndex(CountDatabase.COLUMN_COUNT));
                    int newCount = oldCount + 1;
                    values.put(CountDatabase.COLUMN_COUNT, newCount);
                    mContext.getContentResolver().update(CountProvider.getUri(), values, selection, selectionArgs);
                }
            } finally {
                c.close();
            }

            return null;
        }

    }
}
