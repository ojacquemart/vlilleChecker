package com.vlille.checker.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.search.SearchableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to bookmark stations.
 */
public class AllStationsFragment extends StationsListFragment implements SearchableComponent.ViewParent {

    /**
     * The original overall stations list, used for the filter by name.
     */
    private List<Station> originalStations;

    private SearchableComponent searchableComponent;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.searchableComponent = new SearchableComponent(this);
        this.searchableComponent.init();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.searchableComponent.hideInputMethodManager();
    }

    @Override
    protected int getSwipeableResource() {
        return R.layout.swipeable_list;
    }

    @Override
    void loadStations() {
        originalStations = stationEntityManager.findAll();
        setStations(new ArrayList<Station>(originalStations));
    }

    @Override
    public void initListAdapter() {
        /*
         * setListAdapter(null) is a a hack to avoid java.lang.IllegalStateException: Cannot add header view to list -- setListAdapter has already been called.
	     * @see <a href="http://stackoverflow.com/questions/5704478/best-place-to-addheaderview-in-listfragment">Add heaver view in list fragment</a>
         */
        setListAdapter(null);

        final ListView listView = getListView();

        // Header is above the XML code.
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.stations_list_searchfield, listView, false);
        listView.addHeaderView(header, null, false);

        setListAdapter();
    }

    @Override
    public List<Station> getOriginalStations() {
        return originalStations;
    }

    @Override
    public void afterFilterElements() {
        setListAdapter();
        updateVisibleItemsAsRunnable();
    }

    @Override
    public void showNoResultMessage() {
        getHomeActivity().showSnackBarMessage(R.string.search_no_result);
    }

    @Override
    public ImageButton getClearButton() {
        return (ImageButton) getActivity().findViewById(R.id.list_search_field_clear);
    }

    @Override
    public EditText getSearchField() {
        return (EditText) getActivity().findViewById(R.id.list_search_field);
    }

    @Override
    boolean isReadOnly() {
        return true;
    }

}
