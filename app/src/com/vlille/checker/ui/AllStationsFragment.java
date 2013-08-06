package com.vlille.checker.ui;

import java.util.ArrayList;
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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.utils.StationUtils;
import com.vlille.checker.utils.ToastUtils;

/**
 * A fragment to bookmark stations.
 * TODO: create a base fragment for the users stations and the selectable stations.
 */
public class AllStationsFragment extends SherlockListFragment implements AbsListView.OnScrollListener {

	private static final String TAG = AllStationsFragment.class.getName();

    /**
     * The current activity.
     */
	private Activity activity;

    /**
     * The original overall stations list, used for the filter by name.
     */
	private List<Station> originalStations;

    /**
     * The stations list used by the adapter.
     */
    private List<Station> stations;

    /**
     * The list view adapter.
     */
    private StarsListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        activity = getActivity();
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

		refreshStations();
		
		addHeaderEditText();
		initSearchFieldListeners();
		initFastScroll();
		setFullAdapter();
	}

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateAsRunnableVisibleItems(stations);
    }

    private void refreshStations() {
        originalStations = VlilleChecker.getDbAdapter().findAll();
		stations = new ArrayList<Station>(originalStations);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		hideInputMethodManager();
	}

	/**
	 * setListAdapter(null) is a a hack to avoid java.lang.IllegalStateException: Cannot add header view to list -- setAdapter has already been called.
	 * @see <a href="http://stackoverflow.com/questions/5704478/best-place-to-addheaderview-in-listfragment">Add heaver view in list fragment</a>
	 */
	private void addHeaderEditText() {
		setListAdapter(null);
		final ListView listView = getListView();
        listView.setOnScrollListener(this);

        // Header is above the XML code.
        final LayoutInflater inflater = activity.getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.edit_text, listView, false);
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
				return event.getAction() == KeyEvent.ACTION_DOWN
						    && keyCode == KeyEvent.KEYCODE_ENTER;
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

		final List<Station> filteredStations = StationUtils.filter(originalStations, keyword);
		if (filteredStations.isEmpty()) {
			ToastUtils.show(getActivity(), R.string.search_no_result);
		} else {
            stations = filteredStations;
		}

        setAdapter();
        updateAsRunnableVisibleItems(filteredStations);
    }
	
	private void setFullAdapter() {
		setAdapter();
	}
	
	private void setAdapter() {
        adapter = new StarsListAdapter(activity, R.layout.stars_list_content, stations);
        adapter.setReadOnly(true);
        setListAdapter(adapter);
	}

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            updateVisibleItems();
        }
        Log.d(TAG, "onScrollStateChanged with state " + scrollState);
    }

    /**
     * Update visible stations using the ListView#post method to get the correct last visible item position.
     */
    private void updateAsRunnableVisibleItems(final List<Station> stations) {
        getListView().post(new Runnable() {
            @Override
            public void run() {
                updateVisibleItems();
            }
        });
    }


    /**
     * Update visible stations.
     */
    private void updateVisibleItems() {
        int lastVisibileRow = getListView().getLastVisiblePosition();
        Log.d(TAG, "Last visible row = " + lastVisibileRow);

        if (lastVisibileRow != -1) {
            int firstVisibleRow = getListView().getFirstVisiblePosition();
            List<Station> subStations = stations.subList(firstVisibleRow, lastVisibileRow);

            new AsyncStationsRetriever().execute(subStations);

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
    }

    class AsyncStationsRetriever extends AbstractAsyncStationTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            getSherlockActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "Progress update...");
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Station> result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }
    }
}
