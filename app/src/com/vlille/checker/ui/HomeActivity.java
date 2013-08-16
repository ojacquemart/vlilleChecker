package com.vlille.checker.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Window;
import com.vlille.checker.R;
import com.vlille.checker.db.DBFiller;
import com.vlille.checker.db.DBUpdater;
import com.vlille.checker.ui.listener.TabListener;
import com.vlille.checker.utils.ToastUtils;

import org.droidparts.activity.sherlock.FragmentActivity;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshAttacher;

/**
 * Home activity.
 */
public class HomeActivity extends FragmentActivity {

	private static final String TAG = HomeActivity.class.getSimpleName();

    private PullToRefreshAttacher mPullToRefreshAttacher;

    PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        setPullToRefreshAttacher();
        checkDbInitialization();
        initTabs();
        initSherlockProgressBar();
    }

    private void setPullToRefreshAttacher() {
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
    }

    private void checkDbInitialization() {
        DBFiller dbFiller = new DBFiller(getApplicationContext());
        if (dbFiller.isDBEmpty()) {
            dbFiller.fill();
        }
    }
	
	private void initTabs() {
		ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_menu_icon);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.ic_tab_star)
				.setTabListener(new TabListener<StarsListFragment>(this, "stars", StarsListFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.ic_tab_view_as_list_white)
				.setTabListener(new TabListener<AllStationsFragment>(this, "list", AllStationsFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setIcon(R.drawable.ic_tab_map_white)
				.setTabListener(new TabListener<MapFragment>(this, "map", MapFragment.class)));
	}
	
	private void initSherlockProgressBar() {
		getSherlock().setProgressBarIndeterminate(false);
		getSherlock().setProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    Log.d(TAG, "onConfigurationChanged");
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
		
		// Contextual menus
		
		// Preferences
		menu.add(getString(R.string.preferences)).setIcon(R.drawable.ic_menu_settings)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), HomePreferenceActivity.class));
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		// Update stations data.
		menu.add(getString(R.string.data_launch_update)).setIcon(R.drawable.ic_menu_import_export)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					Log.d(TAG, "Launch data update");
					new DbUpdaterAsyncTask().execute();
					
					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		
		// About
		menu.add(getString(R.string.about_title)).setIcon(R.drawable.ic_menu_about)
			.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
					startActivity(new Intent(getApplicationContext(), AboutActivity.class));

					return false;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	/**
	 * {@link AsyncTask} to refresh stations from vlille.fr.
	 */
	class DbUpdaterAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
            setActionBarLoadingIndicatorVisible(true);
		}

        @Override
        protected Boolean doInBackground(Void... params) {
            DBUpdater dbUpdater = new DBUpdater(getApplicationContext());

            return dbUpdater.update();
        }

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
            setActionBarLoadingIndicatorVisible(false);

            int resourceId = result
                    ?  R.string.data_status_update_done
                    : R.string.data_status_uptodate;

            ToastUtils.show(getApplicationContext(), resourceId);
		}

	}

}
