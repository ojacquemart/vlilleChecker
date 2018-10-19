package com.vlille.checker.ui.fragment;

import com.vlille.checker.R;
import com.vlille.checker.manager.AnalyticsManager;

/**
 * A fragment to display the details from the bookmarked stations.
 */
public class StarsListFragment extends StationsListFragment {

    @Override
    public void onStart() {
        super.onStart();
        AnalyticsManager.trackScreenView("Stars List Screen");
    }

    @Override
    protected int getSwipeableResource() {
        return R.layout.stations_list_layout;
    }

    @Override
    public void initListAdapter() {
        super.initListAdapter();
        getListView().setEmptyView(getActivity().findViewById(android.R.id.empty));
    }

    @Override
    protected void loadStations() {
        setStations(stationEntityManager.findAllStarred());
    }

    @Override
    protected boolean isReadOnly() {
        return false;
    }

}