package com.vlille.checker.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vlille.checker.R;
import com.vlille.checker.db.DBFiller;
import com.vlille.checker.ui.async.AsyncTaskResultListener;
import com.vlille.checker.ui.async.DBUpdaterAsyncTask;
import com.vlille.checker.ui.fragment.AllStationsFragment;
import com.vlille.checker.ui.fragment.MapFragment;
import com.vlille.checker.ui.fragment.StarsListFragment;
import com.vlille.checker.ui.listener.TabListener;
import com.vlille.checker.utils.ContextHelper;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

/**
 * Home activity.
 */
public class HomeActivity extends ActionBarActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private MenuItem refreshItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        setPullToRefreshAttacher();
        checkDbInitialization();
        initTabs();
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (refreshItem == null) {
            return;
        }

        if (refreshing) {
            MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
        } else {
            MenuItemCompat.setActionView(refreshItem, null);
        }
    }

    private void setPullToRefreshAttacher() {
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
    }

    private void checkDbInitialization() {
        DBFiller.fillIfDbIsEmpty();
    }

    private void initTabs() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_menu_icon);
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main_menu, menu);

        initRefreshItem(menu);

        return true;
    }

    private void initRefreshItem(Menu menu) {
        refreshItem = menu.findItem(R.id.main_menu_refresh);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.home_content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                showIntent(SettingsActivity.class);
                break;
            case R.id.main_menu_refresh:
                getCurrentFragment().onResume();
                break;
            case R.id.main_menu_update_stations:
                launchUpdateStations();
                break;
            case R.id.main_menu_about:
                showIntent(AboutActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showIntent(Class<?> clazz) {
        startActivity(new Intent(getApplicationContext(), clazz));
    }

    private void launchUpdateStations() {
        if (ContextHelper.isNetworkAvailable(getApplicationContext())) {
            final AsyncTaskResultListener<Boolean> listener = new AsyncTaskResultListener<Boolean>() {
                @Override
                public void onAsyncTaskPreExecute() {
                    setRefreshActionButtonState(true);
                }

                @Override
                public void onAsyncTaskPostExecute(Boolean result) {
                    setRefreshActionButtonState(false);
                }
            };

            Log.d(TAG, "Launch data update");

            DBUpdaterAsyncTask dbUpdaterAsyncTask = new DBUpdaterAsyncTask();
            dbUpdaterAsyncTask.setAsyncListener(listener);
            dbUpdaterAsyncTask.execute();
        }
    }

    public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }

}