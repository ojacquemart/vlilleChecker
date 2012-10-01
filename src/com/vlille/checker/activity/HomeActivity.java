package com.vlille.checker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;

/**
 * Home Vlille Checker activity.
 */
public class HomeActivity extends SherlockFragmentActivity {

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		setContentView(R.layout.home);
		buildTabs();
	}
	
	private void buildTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.star)
				.setTabListener(new TabListener<StarsListFragment>(this, "stars", StarsListFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.view_as_list)
				.setTabListener(new TabListener<AllStationsFragment>(this, "list", AllStationsFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.map)
				.setTabListener(new TabListener<MapFragment>(this, "map", MapFragment.class)));
	}

	/**
	 * Create contextual menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Refresh stations status.
		menu.add(getString(R.string.refresh)).setIcon(R.drawable.refresh_selector)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					/**
					 * @see TabListener for android.R.id.content
					 */
					final Fragment currentFragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
					currentFragment.onResume();
					
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		// Contextuel menus
		
		// Preferences
		menu.add(getString(R.string.preferences)).setIcon(R.drawable.ic_menu_preferences_ics)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), HomePreferenceActivity.class));
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// Update stations data.
		menu.add(getString(R.string.data_launch_update)).setIcon(R.drawable.import_export)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					Log.d(TAG, "Launch data update");
					VlilleChecker.getDbAdapter().checkStationsUpdate();
					
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		
		// About
		menu.add(getString(R.string.about_title)).setIcon(R.drawable.ic_menu_info_details_ics)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), AboutActivity.class));

					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

}
