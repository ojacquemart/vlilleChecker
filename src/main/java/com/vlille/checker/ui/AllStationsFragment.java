package com.vlille.checker.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
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

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.StationUtils;
import com.vlille.checker.utils.ToastUtils;

/**
 * All stations fragment UI, which displays all the existing stations.
 */
public class AllStationsFragment extends SherlockListFragment {

	public static final String PREFS_FILE = "VLILLE_PREFS";
	
	private final String TAG = getClass().getSimpleName();
	
	private Activity activity;
	private List<Station> stations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = getActivity();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshStations();
		
		addHeaderEditText();
		initSearchFieldListeners();
		initFastScroll();
		setFullAdapter();
	}

	private void refreshStations() {
		stations = VlilleChecker.getDbAdapter().findAll();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		hideInputMethodManager();
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
					hideInputMethodManager();
					
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
	
	private void hideInputMethodManager() {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getSearchField().getWindowToken(), 0);
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
		final List<Station> filteredStations = StationUtils.filter(stations, keyword);
		if (filteredStations.isEmpty()) {
			ToastUtils.show(getActivity(), R.string.search_no_result);
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
	
}
