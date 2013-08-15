package com.vlille.checker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;

import org.droidparts.persist.sql.AbstractDBOpenHelper;

public class DbOpenHelper extends AbstractDBOpenHelper {

    private static final String TAG = DbOpenHelper.class.getSimpleName();

    public DbOpenHelper(Context ctx) {
        super(ctx, DB.FILE, DB.VERSION);
    }

    @Override
    protected void onCreateTables(SQLiteDatabase db) {
        Log.d(TAG, "onCreateTables");
        createTables(db, Station.class);
        createTables(db, Metadata.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        dropTables(db);
        onCreate(db);
    }

}
