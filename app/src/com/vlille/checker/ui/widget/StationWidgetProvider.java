package com.vlille.checker.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.MapStationWidget;
import com.vlille.checker.model.Station;

public class StationWidgetProvider extends AppWidgetProvider {

    private static final String TAG = StationWidgetProvider.class.getSimpleName();

    public static final String ACTION_REFRESH = "com.vlille.checker.widget.Provider.action.REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(TAG, "onReceive " + intent.getAction());
        if (isSupportedAction(intent)) {
            setupWidgets(context, AppWidgetManager.getInstance(context));
        }
    }

    private boolean isSupportedAction(Intent intent) {
        return Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_USER_PRESENT.equals(intent.getAction())
                || ACTION_REFRESH.equals(intent.getAction());
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        setupWidgets(context, appWidgetManager);
    }

    private void setupWidgets(Context context, AppWidgetManager appWidgetManager) {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StationWidgetProvider.class));

        if (appWidgetIds.length > 0) {
            refreshWidgets(context, appWidgetIds);
        }
    }

    private void refreshWidgets(Context context, int[] appWidgetIds) {
        StationEntityManager stationEntityManager = new StationEntityManager(context);
        MapStationWidget stationWithAppWidgetIds = stationEntityManager.findAllWithAppWidget();

        for (int appWidgetId : appWidgetIds) {
            Station station = stationWithAppWidgetIds.get(appWidgetId);
            if (station != null) {
                Log.v(TAG, "Update widget= " + appWidgetId + " with station=" + station.getId());

                StationWidgetUpdater stationWidgetUpdater = new StationWidgetUpdater(station,  context);
                stationWidgetUpdater.update();
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        int appWidgetId = appWidgetIds[0];
        Log.d(TAG, "Delete widget: " + appWidgetId);

        StationEntityManager stationEntityManager = new StationEntityManager(context);
        MapStationWidget mapStationWidget = stationEntityManager.findAllWithAppWidget();
        Station station = mapStationWidget.get(appWidgetId);
        if (station != null) {
            station.setAppWidgetId(Station.APPWIDGET_ID_EMPTY_VALUE);
            Log.d(TAG, "Remove app widget id for station: " + station.getId());

            stationEntityManager.update(station);
        }
    }

}
