package com.vlille.checker.ui.fragment;

import com.vlille.checker.R;

/**
 * A fragment to display the details from the bookmarked stations.
 */
public class StarsListFragment extends StationsListFragment {

    private static final String TAG = StarsListFragment.class.getName();

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
    void loadStations() {
        setStations(stationEntityManager.findAllStarred());
    }

    @Override
    boolean isReadOnly() {
        return false;
    }

}