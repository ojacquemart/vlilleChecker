package com.vlille.checker.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.vlille.checker.model.Metadata;
import com.vlille.checker.model.Station;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

public class DBOpenHelper extends AbstractDBOpenHelper {

    private static final String TAG = DBOpenHelper.class.getSimpleName();

    public DBOpenHelper(Context ctx) {
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
        if (newVersion == 2) {
            // Version 2 adds Station#appWidgetId
            Log.d(TAG, "addMissingColumns");
            addMissingColumns(db, Station.class);
        }
    }

}

