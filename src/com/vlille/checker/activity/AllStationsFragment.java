package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.db.station.StationTableFields;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.MiscUtils;
import com.vlille.checker.utils.StationFilter;

/**
 * All stations fragment UI, which displays all the existing stations.
 */
public class AllStationsFragment extends VlilleSherlockListFragment {

	public static final String PREFS_FILE = "VLILLE_PREFS";
	
	private List<Station> stations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		stations = VlilleChecker.getDbAdapter().findAll();
		addHeaderEditText();
		initSearchFieldListeners();
		initFastScroll();
		setFullAdapter();
	}

	/**
	 * setListAdapter(null) is a a hack to avoid java.lang.IllegalStateException: Cannot add header view to list -- setAdapter has already been called.
	 * @see http://stackoverflow.com/questions/5704478/best-place-to-addheaderview-in-listfragment
	 */
	private void addHeaderEditText() {
		setListAdapter(null);
		final ListView listView = getListView();
		final LayoutInflater inflater = activity.getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.edit_text, listView, false); //header is above XML code
		listView.addHeaderView(header, null, false);
	}

	private void initSearchFieldListeners() {
		initSearchTextListener();
		initClearTextListener();
	}

	private void initSearchTextListener() {
		final EditText searchField = getSearchField();
		searchField.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(TAG, "onKeyListener " + event);
				if (hasPressedOk(keyCode, event)) {
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
					
					final String keyword = searchField.getText().toString();
					filterStationsByKeyword(keyword);
				}
				
				return false;
			}

			private boolean hasPressedOk(int keyCode, KeyEvent event) {
				return (event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER);
			}
		});
	}
	
	private void initClearTextListener() {
		final ImageButton clearButton = (ImageButton) activity.findViewById(R.id.list_search_field_clear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "clear search editText");
				getSearchField().setText(null);
				filterStationsByKeyword(null);
			}
		});
	}
	
	private EditText getSearchField() {
		final EditText searchField = (EditText) activity.findViewById(R.id.list_search_field);
		return searchField;
	}
	
	private void initFastScroll() {
		getListView().setFastScrollEnabled(true);
	}

	private void filterStationsByKeyword(final String keyword) {
		Log.d(TAG, "Text searched: " + keyword);
		final List<Station> filteredStations = StationFilter.doFilter(stations, keyword);
		if (filteredStations.isEmpty()) {
			Toast.makeText(activity, R.string.search_no_result, Toast.LENGTH_SHORT).show();
			setFullAdapter();
		} else {
			setAdapter(filteredStations);
		}
	}
	
	private void setFullAdapter() {
		setAdapter(stations);
	}
	
	private void setAdapter(List<Station> stations) {
		setListAdapter(new AllStationsAdapter(activity, R.layout.all_stations_list_content, stations));
	}

	/*
	 * The followings methods are dead code from an older version
	 * when AllStations was an activity et the UI provided
	 * a button to search stations by its name.
	 */
	
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
		filterStationsByKeyword(query);
	}


	private void handleIntentActionView(Intent intent) {
		Uri uri = intent.getData();
		Log.d(TAG, "uri " + uri.getQuery());
		Cursor cursor = activity.managedQuery(uri, null, null, null, null);
		if (cursor == null) {
			Toast.makeText(activity, R.string.search_suggestion_error, Toast.LENGTH_SHORT).show();
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
			Log.d(TAG, "Station not found: " + stationId);
		}

		setAdapter(suggestedStations);
	}
	
}
