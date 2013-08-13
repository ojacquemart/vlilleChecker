package com.vlille.checker.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;

/**
 * A fragment to display the details from the bookmarked stations.
 */
public class StarsListFragment extends StationsListFragment {

    private static final String TAG = StarsListFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        inflater.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.stations_list_layout, container, false);

        return view;
    }

    @Override
    public void initListAdapter() {
        super.initListAdapter();
        getListView().setEmptyView(getActivity().findViewById(android.R.id.empty));
    }

    @Override
    void loadStations() {
        setStations(VlilleChecker.getDbAdapter().getStarredStations());
    }

    @Override
    boolean isReadOnly() {
        return false;
    }

}
