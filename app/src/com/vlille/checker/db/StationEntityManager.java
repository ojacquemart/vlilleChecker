package com.vlille.checker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import com.vlille.checker.model.MapStationWidget;
import com.vlille.checker.model.Station;
import org.droidparts.persist.sql.EntityManager;
import org.droidparts.persist.sql.stmt.Is;

import java.util.List;

public class StationEntityManager extends EntityManager<Station> {

    private static final String TAG = StationEntityManager.class.getSimpleName();
    private int appWigetIdToNull;

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
        return readAll(select()
                .where(Station.STARRED, Is.EQUAL, true)
                .orderBy(Station.NAME, true)
        );
    }

    public List<Station> findAllWithoutAppWidget() {
        return readAll(select()
                .where(Station.APPWIDGET_ID, Is.EQUAL, Station.APPWIDGET_ID_EMPTY_VALUE)
                .orderBy(Station.STARRED, false)
                .orderBy(Station.NAME, true)
        );
    }

    public MapStationWidget findAllWithAppWidget() {
        List<Station> stations = readAll(select()
                .where(Station.APPWIDGET_ID, Is.NOT_NULL)
                .where(Station.APPWIDGET_ID, Is.NOT_EQUAL, -1));

        return new MapStationWidget(stations);
    }

    @Override
    public boolean create(Station item) {
        createForeignKeys(item);
        ContentValues cv = toContentValues(item);

        long id = 0;
        try {
            id = getDB().insertOrThrow(getTableName(), null, cv);
        } catch (SQLException e) {
            Log.d(TAG, "Error during insert", e);
        }
        if (id > 0) {
            item.id = id;
            return true;
        }

        return false;
    }

}
