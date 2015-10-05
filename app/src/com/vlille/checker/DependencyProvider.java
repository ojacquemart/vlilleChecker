package com.vlille.checker;

import android.content.Context;
import android.util.Log;
import com.vlille.checker.db.DBOpenHelper;
import com.vlille.checker.db.MetadataEntityManager;
import com.vlille.checker.db.StationEntityManager;
import org.droidparts.AbstractDependencyProvider;
import org.droidparts.persist.sql.AbstractDBOpenHelper;

public class DependencyProvider extends AbstractDependencyProvider {

    private static final String TAG = DependencyProvider.class.getSimpleName();

    private final DBOpenHelper dbOpenHelper;

    private StationEntityManager stationEntityManager;
    private MetadataEntityManager metadataEntityManager;

    public DependencyProvider(Context ctx) {
        super(ctx);

        Log.d(TAG, "Dependency provider initialization...");
        dbOpenHelper = new DBOpenHelper(ctx);
    }

    @Override
    public AbstractDBOpenHelper getDBOpenHelper() {
        return dbOpenHelper;
    }

    public StationEntityManager getStationEntityManager(Context ctx) {
        if (stationEntityManager == null) {
            stationEntityManager = new StationEntityManager(ctx);
        }

        return stationEntityManager;
    }

    public MetadataEntityManager getMetadataEntityManager(Context ctx) {
        if (metadataEntityManager == null) {
            metadataEntityManager = new MetadataEntityManager(ctx);
        }

        return metadataEntityManager;
    }

    @Override
    protected Context getContext() {
        return super.getContext();
    }
}
