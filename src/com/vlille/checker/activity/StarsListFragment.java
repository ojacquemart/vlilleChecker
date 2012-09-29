package com.vlille.checker.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.service.AbstractRetrieverService;
import com.vlille.checker.service.StationsResultReceiver;
import com.vlille.checker.service.StationsResultReceiver.Receiver;
import com.vlille.checker.service.StationsRetrieverService;
import com.vlille.checker.utils.ContextHelper;

/**
 * Fragment activity wich displays starred activities.
 */
public class StarsListFragment extends SherlockListFragment implements Receiver {

	private final String TAG = getClass().getSimpleName();
	
	private FragmentActivity activity;
	private StationsResultReceiver resultReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		activity = getActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		setStarsAdapter();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		setStarsAdapter();
	}

	private void setStarsAdapter() {
		final List<Station> starredStations = VlilleChecker.getDbAdapter().getStarredStations();
		
		boolean isEmptyStarredStations = starredStations.isEmpty();
		Log.d(TAG, "Starred stations empty? " + isEmptyStarredStations);
		if (isEmptyStarredStations) {
			showBoxNewStation(null);
		} else {
			handleStarredStations(starredStations);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void handleStarredStations(List<Station> starredIdsStations) {
		if (ContextHelper.isNetworkAvailable(activity)) {
			if (!activity.isFinishing()) {
				 setListShownNoAnimation(false);
			}

			Log.d(TAG, "Start retriever service.");
			resultReceiver = new StationsResultReceiver(new Handler());
			resultReceiver.setReceiver(this);

			final Intent intent = new Intent(Intent.ACTION_SYNC, null, activity,
					StationsRetrieverService.class);
			intent.putExtra(RECEIVER, resultReceiver);
			intent.putExtra(AbstractRetrieverService.EXTRA_DATA, (ArrayList<Station>) starredIdsStations);
			activity.startService(intent);
		} else {
			Log.d(TAG, "No network, show the retry view");

			showBoxError(true);
		}
	}

	/**
	 * TODO: add loader.
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		boolean finished = false;
		boolean error = false;

		switch (resultCode) {
		case Receiver.RUNNING:
			Log.d(TAG, "Retrieve in progress");

			break;
		case Receiver.FINISHED:
			Log.d(TAG, "All starred stations loaded");
			finished = true;

			@SuppressWarnings("unchecked")
			List<Station> results = (List<Station>) resultData.getSerializable(AbstractRetrieverService.RESULTS);

			showBoxNewStation(results);
			handleAdapter(results);

			break;
		case Receiver.ERROR:
			finished = true;
			error = true;

			break;
		}

		if (finished && !activity.isFinishing()) {
			setListShownNoAnimation(true);
		}

		showBoxError(error);
	}

	/**
	 * Handle adapter listview
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void handleAdapter(final List<Station> stations) {
		final StarsListAdapter adapter = new StarsListAdapter(activity, R.layout.stars_list_content, stations, null);
		setListAdapter(adapter);
	}

	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void showBoxNewStation(List<Station> stations) {
		boolean show = stations == null || stations.isEmpty();

//		MiscUtils.showOrMask((LinearLayout) activity.findViewById(R.id.home_station_new_box), show);
	}

	/**
	 * Display the add new button if there are no stations in preferences.
	 * 
	 * @param stations
	 *            The starred stations details.
	 */
	private void showBoxError(boolean show) {
		// TODO: see how to handle new station and error message
//		MiscUtils.showOrMask((RelativeLayout) activity.findViewById(R.id.home_error_box), show);
	}
	
}
