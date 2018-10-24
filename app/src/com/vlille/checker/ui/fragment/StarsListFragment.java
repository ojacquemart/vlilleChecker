package com.vlille.checker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.manager.AnalyticsManager;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.fragment.adapter.StationsAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment to display the details from the bookmarked stations.
 */
public class StarsListFragment extends StationsListFragment {

    TextView sortDirectionTextView;
    Boolean isSortingAscending;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureSortDirection(view);
    }

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

    private void configureSortDirection(View rootView) {
        sortDirectionTextView = rootView.findViewById(R.id.sort_direction);
        sortAscending();
        sortDirectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSortingAscending) {
                    sortDescending();
                } else {
                    sortAscending();
                }
            }
        });
    }

    private void sortAscending() {
        List<Station> stations = getStations();
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station station1, Station station2) {
                if (station1.getName().toCharArray()[0] > station2.getName().toCharArray()[0]) {
                    return 1;
                } else if (station1.getName().toCharArray()[0] < station2.getName().toCharArray()[0]) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        StationsAdapter adapter = (StationsAdapter) getListAdapter();
        adapter.notifyDataSetChanged();
        isSortingAscending = true;
        sortDirectionTextView.setText("A-Z");
        sortDirectionTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_upward), null);
    }

    private void sortDescending() {
        List<Station> stations = getStations();
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station station1, Station station2) {
                if (station1.getName().toCharArray()[0] > station2.getName().toCharArray()[0]) {
                    return -1;
                } else if (station1.getName().toCharArray()[0] < station2.getName().toCharArray()[0]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        StationsAdapter adapter = (StationsAdapter) getListAdapter();
        adapter.notifyDataSetChanged();
        isSortingAscending = false;
        sortDirectionTextView.setText("Z-A");
        sortDirectionTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_downward), null);
}

}