package com.vlille.checker.ui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.github.mrengineer13.snackbar.SnackBar;
import com.vlille.checker.R;
import com.vlille.checker.db.StationEntityManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.search.SearchableComponent;
import org.droidparts.Injector;
import org.droidparts.annotation.inject.InjectDependency;

import java.util.List;

public class StationWidgetConfigurationActivity extends ActionBarActivity implements SearchableComponent.ViewParent {

    private static final String TAG = StationWidgetConfigurationActivity.class.getSimpleName();

    @InjectDependency
    private StationEntityManager stationEntityManager;

    private int appWidgetId;

    private List<Station> originalStations;
    private List<Station> filteredStations;
    private ListView listView;
    private SearchableComponent searchableComponent;
    private SnackBar snackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(getApplicationContext(), this);

        setContentView(R.layout.widget_station_list_layout);

        storeWidgetId();
        initStations();
        initListView();
        initSearchableComponent();
        initSnackbar();
    }

    @Override
    protected void onPause() {
        super.onPause();

        searchableComponent.hideInputMethodManager();
    }

    private void initSearchableComponent() {
        this.searchableComponent = new SearchableComponent(this);
        searchableComponent.init();
    }

    private void initSnackbar() {
        this.snackBar = new SnackBar(this);
    }

    private void initStations() {
        this.originalStations = stationEntityManager.findAllWithoutAppWidget();
        setStations(this.originalStations);
    }

    private void storeWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Log.d(TAG, "Widget id:" + appWidgetId);
    }

    private void initListView() {
        this.listView = (ListView) findViewById(R.id.list);
        setListAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "Click on station position: " + position);

                Station station = filteredStations.get(position);
                handleStationSelection(station);
            }
        });
    }

    private void setListAdapter() {
        SelectableStationAdapter adapter = new SelectableStationAdapter(getApplicationContext(), R.layout.station_list_item, filteredStations);
        listView.setAdapter(adapter);
    }

    private void handleStationSelection(Station station) {
        Log.i(TAG, "Station clicked: " + station.getName());

        updateWidgetData(station);

        Log.d(TAG, "Finish station selection");
        setResult(RESULT_OK, getResultIntent());

        finish();
    }

    private Intent getResultIntent() {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, StationWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        return intent;
    }

    private void updateWidgetData(Station station) {
        saveStationWidgetId(station);

        StationWidgetUpdater stationWidgetUpdater = new StationWidgetUpdater(station, getApplicationContext());
        stationWidgetUpdater.update();
    }

    private void saveStationWidgetId(Station station) {
        station.setAppWidgetId(appWidgetId);
        Log.d(TAG, "Persist widget station: " + station.getId());

        stationEntityManager.update(station);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void afterFilterElements() {
        setListAdapter();
    }

    @Override
    public List<Station> getOriginalStations() {
        return originalStations;
    }

    @Override
    public void setStations(List<Station> stations) {
        this.filteredStations = stations;
    }

    @Override
    public ImageButton getClearButton() {
        return (ImageButton) findViewById(R.id.list_search_field_clear);
    }

    @Override
    public EditText getSearchField() {
        return (EditText) findViewById(R.id.list_search_field);
    }

    @Override
    public void showNoResultMessage() {
        snackBar.clear();
        snackBar.show(getString(R.string.search_no_result));
    }
}
