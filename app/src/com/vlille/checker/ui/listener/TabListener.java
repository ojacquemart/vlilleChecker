package com.vlille.checker.ui.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * From http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
 *
 * @author draudemorvan
 */
public class TabListener<T extends Fragment> implements ActionBar.TabListener {

    private final SherlockFragmentActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    private Fragment mFragment;

    public TabListener(SherlockFragmentActivity activity, String tag, Class<T> clazz) {
        mActivity = activity;
        mTag = tag;
        mClass = clazz;
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Fragment preInitializedFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
        if (preInitializedFragment == null) {
            mFragment = SherlockFragment.instantiate(mActivity, mClass.getName());

            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            mFragment = preInitializedFragment;
            ft.attach(preInitializedFragment);
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        Fragment preInitializedFragment = mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
        if (preInitializedFragment != null) {
            ft.detach(preInitializedFragment);
        } else if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

    public Fragment getFragment() {
        return mFragment;
    }

}
