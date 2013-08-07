package com.vlille.checker.ui;

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

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * A fragment to bookmark stations.
 * TODO: create a base fragment for the users stations and the selectable stations.
 */
public class AllStationsFragment extends SherlockListFragment
        implements AbsListView.OnScrollListener, PullToRefreshAttacher.OnRefreshListener {

	private static final String TAG = AllStationsFragment.class.getName();

    /**
     * The pullToRefreshAttach.
     */
    private PullToRefreshAttacher pullToRefreshAttacher;

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

    /**
     * The current AsyncTask.
     */
    private StationsAsyncTask asyncTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        activity = getActivity();
        pullToRefreshAttacher = PullToRefreshAttacher.get(activity);
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        pullToRefreshAttacher.addRefreshableView(getListView(), this);

		setStations();

		addHeaderEditText();
		initSearchFieldListeners();
		initFastScroll();
		setListAdapter();
	}

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateAsRunnableVisibleItems(stations);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        hideInputMethodManager();
        cancelAsyncTask();
    }

    private void setStations() {
        originalStations = VlilleChecker.getDbAdapter().findAll();
		stations = new ArrayList<Station>(originalStations);
	}

	/**
	 * setListAdapter(null) is a a hack to avoid java.lang.IllegalStateException: Cannot add header view to list -- setListAdapter has already been called.
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

    /**
     * Hides the input text field when searched for some station.
     */
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
                Log.d(TAG, "Clear search editText");
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

        setListAdapter();
        updateAsRunnableVisibleItems(filteredStations);
    }

	private void setListAdapter() {
        adapter = new StarsListAdapter(activity, R.layout.stars_list_content, stations);
        adapter.setReadOnly(true);
        setListAdapter(adapter);
	}

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        Log.d(TAG, "onScrollStateChanged with state " + scrollState);

        if (scrollState == SCROLL_STATE_IDLE) {
            updateVisibleItems();
        }
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
        Log.d(TAG, "Index of last visible row = " + lastVisibileRow);

        if (lastVisibileRow != -1) {
            Log.d(TAG, "Update only visible stations");

            int firstVisibleRow = getListView().getFirstVisiblePosition();
            List<Station> subStations = stations.subList(firstVisibleRow, lastVisibileRow);

            asyncTask = getNewAsyncTask();
            asyncTask.execute(subStations);
        }
    }

    /**
     * Cancels the maybe running task and gets a new one.
     */
    private StationsAsyncTask getNewAsyncTask() {
        cancelAsyncTask();
        asyncTask = new StationsAsyncTask();

        return asyncTask;
    }

    /**
     * Cancels the maybe running async task.
     */
    private void cancelAsyncTask() {
        if (asyncTask != null) {
            asyncTask.cancel();
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onRefreshStarted(View view) {
    }

    class StationsAsyncTask extends AbstractAsyncStationTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            setProgressBarIndeterminateVisibility(true);
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

            setProgressBarIndeterminateVisibility(false);
        }

        public void cancel() {
            if (getStatus() == Status.RUNNING) {
                Log.d(TAG, "Cancel the async task");
                cancel(false);
                setProgressBarIndeterminateVisibility(false);
            }
        }

        private void setProgressBarIndeterminateVisibility(boolean visible) {
            pullToRefreshAttacher.setRefreshing(visible);
        }
    }
}
