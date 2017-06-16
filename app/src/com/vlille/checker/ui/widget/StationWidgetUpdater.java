package com.vlille.checker.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.StationPreferences;
import com.vlille.checker.utils.color.ColorSelector;

public class StationWidgetUpdater {

    private static final String TAG = StationWidgetUpdater.class.getSimpleName();

    private Station station;

    private Context context;
    private Resources resources;
    private RemoteViews remoteViews;
    private AppWidgetManager appWidgetManager;
    private StationEntityManager stationEntityManager;
    private StationPreferences preferences;

    public StationWidgetUpdater(Station station, Context context) {
        this.station = station;

        this.stationEntityManager = new StationEntityManager(context);

        this.context = context;
        this.preferences = ContextHelper.getPreferences(context);
        this.resources = context.getResources();

        this.remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        this.appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void update() {
        Log.d(TAG, "Update widget stations");

        prepareWidget(station);

        StationUpdateDelegate delegate = new StationUpdateDelegate() {
            @Override
            public void update(Station station) {
                updateWidget(station, false);
            }
        };

        WidgetAsyncTask widgetAsyncTask = new WidgetAsyncTask(delegate);
        widgetAsyncTask.execute(station);
    }

    private void prepareWidget(Station station) {
        Log.v(TAG, "Prepare");

        addOnClickRefresh();

        setStationName(station);
        updateWidget(station, true);
    }

    private void setStationName(Station station) {
        remoteViews.setTextViewText(R.id.station_name, station.getName(preferences.isIdVisible()));
    }

    private void addOnClickRefresh() {
        Log.v(TAG, "Add onClick refresh");

        Intent intentRefresh = new Intent();
        intentRefresh.setAction(StationWidgetProvider.ACTION_REFRESH);

        remoteViews.setOnClickPendingIntent(R.id.widget_station, getPendingIntent(intentRefresh));
    }

    private PendingIntent getPendingIntent(Intent intent) {
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateWidget(Station station, boolean init) {
        Log.v(TAG, "Update widget [id=" + station.getAppWidgetId() + ",stationId=" + station.getId() + "]");
        updateWidgetData(station);

        int gone = init ? View.VISIBLE : View.GONE;
        remoteViews.setViewVisibility(R.id.station_loading, gone);
        updateWidgetView(station.getAppWidgetId());
    }

    private void updateWidgetData(Station station) {
        Log.v(TAG, "Update widget data: " + station.getId());

        if (preferences.isUpdatedAtVisible()) {
            remoteViews.setViewVisibility(R.id.station_lastupdate_box, View.VISIBLE);
            remoteViews.setTextViewText(R.id.station_lastupdate, station.getShortLastUpdateAsString(resources));
        } else {
            remoteViews.setViewVisibility(R.id.station_lastupdate_box, View.GONE);
        }
        remoteViews.setViewVisibility(R.id.station_out_of_service_box, station.getOutOfServiceVisibility());

        remoteViews.setTextViewText(R.id.station_details_bikes, station.getBikesAsString());
        remoteViews.setTextColor(R.id.station_details_bikes, ColorSelector.getColor(context, station.getBikes()));
        remoteViews.setTextViewText(R.id.station_details_attachs, station.getAttachsAsString());
        remoteViews.setTextColor(R.id.station_details_attachs, ColorSelector.getColor(context, station.getAttachs()));

        updateStation(station);
    }

    private void updateStation(Station station) {
        stationEntityManager.update(station);
    }

    private void updateWidgetView(int appWidgetId) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
