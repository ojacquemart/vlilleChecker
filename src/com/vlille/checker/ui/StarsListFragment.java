package com.vlille.checker.ui;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.async.AbstractAsyncStationTask;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.ToastUtils;
import com.vlille.checker.utils.ViewUtils;

/**
 * Fragment activity wich displays starred activities.
 * 
 * @see https://github.com/chrisbanes/Android-PullToRefresh/blob/master/library/src/com/handmark/pulltorefresh/library/IPullToRefresh.java
 */
public class StarsListFragment extends SherlockListFragment	 {

	private final String TAG = getClass().getSimpleName();
	
	private PullToRefreshListView pullRefreshListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "receiveRingerChandeModeAction");
				pullRefreshListView.setOnPullEventListener(getPullEventListener());
			}
		};
		IntentFilter filter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
		getActivity().registerReceiver(receiver, filter);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
	    inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    View view = inflater.inflate(R.layout.stars_list_layout, container, false);
	    
	    return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		initPullToRefreshListView();
	}

	private void initPullToRefreshListView() {
		pullRefreshListView = (PullToRefreshListView) getActivity().findViewById(R.id.stars_pull_refresh_list);
		pullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				pullRefreshListView.setLastUpdatedLabel(DateUtils.formatDateTime(getActivity(),
//						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
//								| DateUtils.FORMAT_SHOW_DATE
//								| DateUtils.FORMAT_ABBREV_ALL));

				setStarsAdapter();
			}
		});
		pullRefreshListView.setOnPullEventListener(getPullEventListener());
	}
	
	private SoundPullEventListener<ListView> getPullEventListener() {
		Log.d(TAG, "getPullEventListener");
		if (isRingerModeNormal()) {
			return new SoundPullEventListener<ListView>(
					getActivity(), R.raw.pull_event, R.raw.release_event_bike);
		}
		
		return null;
	}
	
	private boolean isRingerModeNormal() {
		final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
		if (audioManager == null) {
			return false;
		}
		
		final int ringerMode = audioManager.getRingerMode();
		Log.d(TAG, "Ringer mode " + ringerMode);
		return AudioManager.RINGER_MODE_NORMAL == ringerMode;
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		pullRefreshListView.setRefreshing(true);
		setStarsAdapter();
	}

	private void setStarsAdapter() {
		final List<Station> starredStations = VlilleChecker.getDbAdapter().getStarredStations();
		boolean isEmptyStarredStations = starredStations.isEmpty();
		Log.d(TAG, "Starred stations empty? " + isEmptyStarredStations);
		
		ViewUtils.switchView(getActivity().findViewById(R.id.home_nostations_nfo), isEmptyStarredStations);
		if (!isEmptyStarredStations) {
			loadDetails(starredStations);
		}
	}

	private void loadDetails(List<Station> stations) {
		Log.d(TAG, "loadDetails");
		if (!ContextHelper.isNetworkAvailable(getActivity())) {
			ToastUtils.show(getActivity(), R.string.error_no_connection);
		}
		
		boolean error = false;
		
		try {
			new AsyncListStationReader().execute(stations);
		} catch (Exception e) {
			Log.e(TAG, "handleStarredStations", e);
			error = true;
		}
		
		if (error) {
			ToastUtils.show(getActivity(), R.string.error_connection_expired);
		}
	}
	
	private void handleAdapter(final List<Station> stations) {
		final StarsListAdapter adapter = new StarsListAdapter(getActivity(),
				R.layout.stars_list_content, stations, null);
		 pullRefreshListView.setAdapter(adapter);
		 adapter.notifyDataSetChanged();
		 pullRefreshListView.onRefreshComplete();
	}
	
	class AsyncListStationReader extends AbstractAsyncStationTask {
		
		@Override
		protected void onPostExecute(List<Station> result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute");
			
			handleAdapter(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
		}
		
	}

}
