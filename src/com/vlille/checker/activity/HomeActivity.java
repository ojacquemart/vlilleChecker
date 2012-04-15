package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.StationResultReceiver;
import com.vlille.checker.service.StationResultReceiver.Receiver;
import com.vlille.checker.service.StationsRetrieverService;
import com.vlille.checker.utils.MiscUtils;

/**
 * Home Vlille checker activity.
 */
public class HomeActivity extends VlilleListActivity implements Receiver {

	private static final int QUICK_DIALOG_SEARCH_BY_MAPS = 1;
	private static final int QUICK_DIALOG_SEARCH_BY_LIST = 2;

	private QuickAction quickDialogAction; 
	private StationResultReceiver resultReceiver;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		initActionBar();
		initQuickDialog();
		initButtonsListeners();
		initProgressDialog();
	}

	public void onPause() {
		super.onPause();
		
		if (resultReceiver != null) {
			resultReceiver.setReceiver(null); // Clear receiver so no leaks.
		}
	}
	
	/**
	 * onResume refresh data.
	 * 
	 * @see #onReceiveResult(int, Bundle) for data retrieving.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		final List<String> starredIdsStations = getStarredIdsStations();
		
		boolean isEmptyStarredStations = starredIdsStations.isEmpty();
		Log.d(LOG_TAG_ACTIVITY, "Starred stations empty? " + isEmptyStarredStations);
		if (isEmptyStarredStations) {
			showAddNewButtonBox(null);
		} else {
			handleStarredStations(starredIdsStations);
		}
	}	
	
	private void handleStarredStations(List<String> starredIdsStations) {
		if (isNetworkAvailable()) {
			Log.d(LOG_TAG_ACTIVITY, "Start retriever service.");
			resultReceiver = new StationResultReceiver(new Handler());
			resultReceiver.setReceiver(this);
			
			final Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), StationsRetrieverService.class);
			intent.putExtra(RECEIVER, resultReceiver);
			intent.putExtra(StationsRetrieverService.STATIONS_ID, (ArrayList<String>) starredIdsStations);
			startService(intent);
		} else {
			Log.d(LOG_TAG_ACTIVITY, "No network, show the retry view");
			
			showErrorBox(true);
		}
	}
	
	public void onReceiveResult(int resultCode, Bundle resultData) {
		boolean finished = false;
		boolean error = false;
		
		switch (resultCode) {
		case Receiver.RUNNING:
			Log.d(LOG_TAG_ACTIVITY, "Retrieve in progress");
			
			progressDialog.show();

			break;
		case Receiver.FINISHED:
			Log.d(LOG_TAG_ACTIVITY, "All starred stations loaded");
			finished = true;
			
			List<Station> results = resultData.getParcelableArrayList("results");

			showAddNewButtonBox(results);
			handleAdapter(results);

			break;
		case Receiver.ERROR:
			Log.e(LOG_TAG_ACTIVITY, "Error occured");
			finished = true;
			error = true;

			break;
		}
		
		if (finished) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		
		showErrorBox(error);
	}
	
	private void initActionBar() {
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.addAction(new QuickDialogAction());
		actionBar.addAction(new RefreshAction());
	}
    
	private void initQuickDialog() {
		ActionItem searchByMapsItem = new ActionItem(
				QUICK_DIALOG_SEARCH_BY_MAPS,
				getString(R.string.quickdialog_maps),
				getResources().getDrawable(R.drawable.ic_menu_map));
		ActionItem searchByListItem = new ActionItem(
				QUICK_DIALOG_SEARCH_BY_LIST,
				getString(R.string.quickdialog_list),
				getResources().getDrawable(R.drawable.ic_menu_list));
		
		searchByListItem.setSticky(true);
		searchByMapsItem.setSticky(true);

		quickDialogAction = new QuickAction(this, QuickAction.VERTICAL);
		quickDialogAction.addActionItem(searchByMapsItem);
		quickDialogAction.addActionItem(searchByListItem);

		final Context context = this;
		
		quickDialogAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				Class<?> clazz = null;
				
				switch (actionId) {
				case QUICK_DIALOG_SEARCH_BY_LIST:
					clazz = PreferencesActivity.class;
					break;
				case QUICK_DIALOG_SEARCH_BY_MAPS:
					clazz = MapsActivity.class;
					break;
				}
				
				if (clazz != null) {
					quickDialogAction.dismiss();
					
					Intent intent = new Intent(context, clazz);
					startActivity(intent);
				}
			}
		});
		
		quickDialogAction.setOnDismissListener(new QuickAction.OnDismissListener() {			
			@Override
			public void onDismiss() {
			}
		});
		

	}	

	/**
	 * Init add new station listener and retry listener.
	 */
	private void initButtonsListeners() {
		findViewById(R.id.home_station_new).setOnClickListener(newStationOnClickListener);
		findViewById(R.id.home_error_retry_button).setOnClickListener(retryOnClickListener);
	}

	/**
	 * Event for the preferences activity, which configures the saved stations.
	 */
	private OnClickListener newStationOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			quickDialogAction.show(view);
		}
	};
	
	private OnClickListener retryOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			onResume();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Class<?> clazz = null;
		
		switch (item.getItemId()) {
		case R.id.home_settings:
			clazz = HomeSettingsActivity.class;
			break;
		case R.id.home_about:
			clazz = AboutActivity.class;
			break;
		}
		startActivity(new Intent(this, clazz));

		return true;
	}
	
	private void initProgressDialog() {
		progressDialog = new ProgressDialog(HomeActivity.this);
		progressDialog.setMessage(getString(R.string.loading));
	}	

	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations The starred stations details.
	 */
	private void showAddNewButtonBox(List<Station> stations) {
		boolean show = stations == null || stations.isEmpty();
		
		MiscUtils.showOrMask((LinearLayout) findViewById(R.id.home_station_new_box), show);
	}

	/**
	 * Handle adapter listview
	 * 
	 * @param stations The starred stations details.
	 */
	private void handleAdapter(final List<Station> stations) {
		final LinearLayout boxAddStation = (LinearLayout) findViewById(R.id.home_station_new_box);
		final HomeAdapter adapter = new HomeAdapter(this, R.layout.home_station_details, stations, boxAddStation);
		setListAdapter(adapter);
	}
	
	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations The starred stations details.
	 */
	private void showErrorBox(boolean show) {
		MiscUtils.showOrMask((RelativeLayout) findViewById(R.id.home_error_box), show);
	}	
	
	/** AbsrtactActions class for menu ... */
	
	private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            super(R.drawable.ic_menu_refresh);
        }

        @Override
        public void performAction(View view) {
        	quickDialogAction.dismiss();
        	onResume();
        }

    }	
	
	private class QuickDialogAction extends AbstractAction {
		
		public QuickDialogAction() {
			super(R.drawable.ic_menu_add);
		}
		
		@Override
		public void performAction(View view) {
			quickDialogAction.show(view);
		}
		
	}	
}
