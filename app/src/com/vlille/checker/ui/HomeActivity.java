package com.vlille.checker.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mrengineer13.snackbar.SnackBar;
import com.vlille.checker.R;
import com.vlille.checker.db.DBFiller;
import com.vlille.checker.ui.async.AsyncTaskResultListener;
import com.vlille.checker.ui.async.DBUpdaterAsyncTask;
import com.vlille.checker.ui.fragment.AllStationsFragment;
import com.vlille.checker.ui.fragment.MapFragment;
import com.vlille.checker.ui.fragment.StarsListFragment;
import com.vlille.checker.ui.listener.TabListener;
import com.vlille.checker.utils.ContextHelper;

import java.util.List;

/**
 * Home activity.
 */
public class HomeActivity extends ActionBarActivity implements SnackBar.OnMessageClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private MenuItem refreshItem;
    private SnackBar snackBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        initSnackBar();
        checkDbInitialization();
        initTabs();
    }

    private void initSnackBar() {
        snackBar = new SnackBar(this);
        snackBar.setOnClickListener(this);
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

    private void checkDbInitialization() {
        new DBFiller(this).fillIfDbIsEmpty();
    }

    private void initTabs() {
        ActionBar actionBar = getSupportActionBar();
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
        if (ContextHelper.isNetworkAvailable(this)) {
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

            DBUpdaterAsyncTask dbUpdaterAsyncTask = new DBUpdaterAsyncTask(this);
            dbUpdaterAsyncTask.setAsyncListener(listener);
            dbUpdaterAsyncTask.execute();
        }
    }

    public void showSnackBarMessage(int messageId) {
        snackBar.clear(false);

        snackbarShow(messageId, -1);
    }

    public void showNoConnectionMessage() {
        snackbarShow(R.string.error_no_connection, R.string.retry);
    }

    public void showTranspoleUnstableMessage() {
        snackbarShow(R.string.error_unstable_transpole, R.string.retry);
    }

    private void snackbarShow(int messageId, int actionMessageId) {
        String actionMessage = actionMessageId == - 1 ? null : getString(actionMessageId).toUpperCase();
        snackBar.show(
                getString(messageId),
                actionMessage
        );
    }

    @Override
    public void onMessageClick(Parcelable parcelable) {
        resumeVisibleFragment();
    }

    public void resumeVisibleFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible()) {
                fragment.onResume();
            }
        }
    }

}