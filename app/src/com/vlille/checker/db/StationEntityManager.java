package com.vlille.checker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.vlille.checker.model.Station;

import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;

import java.util.List;

public class StationEntityManager extends EntityManager<Station> {

    private static final String TAG = StationEntityManager.class.getSimpleName();

    public StationEntityManager(Context ctx) {
        super(Station.class, ctx);
    }

    public int count() {
        return select().count();
    }

    public List<Station> findAll() {
        return readAll(select().orderBy(Station.NAME, true));
    }

    public List<Station> findAllStarred() {
        return readAll(select().where(Station.STARRED, Is.EQUAL, true).orderBy(Station.NAME, true));
    }

    @Override
    public boolean create(Station item) {
        createForeignKeys(item);
        ContentValues cv = toContentValues(item);

        long id = 0;
        try {
            id = getDB().insertOrThrow(getTableName(), null, cv);
        } catch (SQLException e) {
            Log.d(TAG, "Erreur during insert", e);
        }
        if (id > 0) {
            item.id = id;
            return true;
        } else {
            return false;
        }
    }


}
