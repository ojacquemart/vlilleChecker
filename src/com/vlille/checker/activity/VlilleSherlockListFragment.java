package com.vlille.checker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;
import com.vlille.checker.R;

public abstract class VlilleSherlockListFragment extends SherlockListFragment {
	
	protected final String TAG = getClass().getSimpleName();
	
	protected Activity activity;

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
		
		hideNoStationsNfo();
	}
	
	public void showNoStationsNfo() {
		getHomeStationsNfo().setVisibility(View.VISIBLE);
	}
	
	public void hideNoStationsNfo() {
		getHomeStationsNfo().setVisibility(View.GONE);
	}
	
	private View getHomeStationsNfo() {
		return activity.findViewById(R.id.home_nostations_nfo);
	}
	
}
