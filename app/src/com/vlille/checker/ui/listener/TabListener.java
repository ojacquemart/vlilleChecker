package com.vlille.checker.ui.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.vlille.checker.R;

/**
 * From http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
 *
 * @author draudemorvan
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {

    private static final String TAG = TabListener.class.getSimpleName();

    private final ActionBarActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    private Fragment mFragment;

    public TabListener(ActionBarActivity activity, String tag, Class<T> clazz) {
        mActivity = activity;
        mTag = tag;
        mClass = clazz;
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d(TAG, "onTabUnselected");
        mFragment = Fragment.instantiate(mActivity, mClass.getName());
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content, mFragment)
                .commit();
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d(TAG, "onTabUnselected");
        Fragment preInitializedFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
        if (preInitializedFragment != null) {
            ft.detach(preInitializedFragment);
        } else {
            detachCurrentFragment(ft);
        }
    }

    private void detachCurrentFragment(FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

    public Fragment getFragment() {
        return mFragment;
    }

}