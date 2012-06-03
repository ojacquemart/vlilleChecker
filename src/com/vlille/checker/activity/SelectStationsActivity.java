package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.db.station.StationTableFields;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.MiscUtils;
import com.vlille.checker.utils.StationFilter;

/**
 * Select stations activity.
 */
public class SelectStationsActivity extends VlilleListActivity {

	public static final String PREFS_FILE = "VLILLE_PREFS";

	private List<Station> stations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContent();
		initActionBar();

		stations = VlilleChecker.getDbAdapter().findAll();
		setAdapter(stations);

		addOkOnClickListener();
	}

	private void initContent() {
		setContentView(R.layout.home_search);
		getListView().setFastScrollEnabled(true);
	}
	
	private void initActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new SearchAction());
	}	

	private void addOkOnClickListener() {
		findViewById(R.id.prefs_ok).setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (intent != null) {
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				handleIntentActionSearch(intent);
			} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				handleIntentActionView(intent);
			}
		}
	}

	private void handleIntentActionSearch(Intent intent) {
		final String query = intent.getStringExtra(SearchManager.QUERY);
		Log.d(LOG_TAG, query);
		
		final List<Station> filteredStations = StationFilter.doFilter(stations, query);
		if (filteredStations.isEmpty()) {
			Toast.makeText(this, R.string.search_no_result, Toast.LENGTH_SHORT).show();
			setFullAdapter();
		} else {
			setAdapter(filteredStations);
		}
	}

	private void handleIntentActionView(Intent intent) {
		Uri uri = intent.getData();
		Log.d(LOG_TAG, "uri " + uri.getQuery());
		Cursor cursor = managedQuery(uri, null, null, null, null);
		if (cursor == null) {
			Toast.makeText(this, R.string.search_suggestion_error, Toast.LENGTH_SHORT).show();
			setFullAdapter();
			
			return;
		}
		
		
		final Map<String, Station> mapStations = MiscUtils.toMap(stations);
		final List<Station> suggestedStations = new ArrayList<Station>();

		int columnIndexStationId = cursor.getColumnIndexOrThrow(StationTableFields._id.toString());
		final String stationId = cursor.getString(columnIndexStationId);
		final Station foundStation = mapStations.get(stationId);
		if (foundStation != null) {
			suggestedStations.add(foundStation);
		} else {
			Log.d(LOG_TAG, "Station not found: " + stationId);
		}

		setAdapter(suggestedStations);
	}

	private void setFullAdapter() {
		setAdapter(stations);
	}
	
	private void setAdapter(List<Station> stations) {
		setListAdapter(new SelectStationsAdapter(this, R.layout.home_search_list_stations, stations));
	}
	
	private class SearchAction extends AbstractAction {
		
		public SearchAction() {
			super(R.drawable.ic_menu_search_ics);
		}
		
		@Override
		public void performAction(View view) {
			onSearchRequested();
		}
		
	}	

}
