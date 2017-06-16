package com.vlille.checker.ui.fragment.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.StationPreferences;
import com.vlille.checker.utils.ViewUtils;
import com.vlille.checker.utils.color.ColorSelector;

import java.util.List;

/**
 * A generic adapter for a stations ListView.
 */
public class StationsAdapter extends ArrayAdapter<Station> {

    private static final String TAG = StationsAdapter.class.getSimpleName();

    private StationUpdateDelegate stationUpdateDelegate;
    private List<Station> stations;
    private Resources resources;
    private boolean readOnly = false;

    public StationsAdapter(Context context, int resource, List<Station> stations) {
        super(context, resource, stations);

        this.stations = stations;
        this.resources = context.getResources();
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        StationPreferences stationPreferences = ContextHelper.getPreferences(getContext());

        if (view == null) {
            LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layout.inflate(R.layout.station_list_item, null);
        }

        setStationAddressVisibility(view, stationPreferences);
        setStationDetails(view, position, stationPreferences);

        return view;
    }

    private void setStationAddressVisibility(View view, StationPreferences stationPreferences) {
        ViewUtils.switchView(view.findViewById(R.id.station_adress_box), stationPreferences.isAddressVisible());
    }

    /**
     * Handle stations details.
     */
    private void setStationDetails(View view, final int position, StationPreferences stationPreferences) {
        if (stations.size() > position) {
            final Station station = stations.get(position);

            handleStarCheckbox(view, position, station);
            handleStationsTextInfos(view, station, stationPreferences);

            ViewUtils.switchView(view.findViewById(R.id.station_lastupdate), stationPreferences.isUpdatedAtVisible());
        }
    }

    private void handleStarCheckbox(View view, final int position, final Station station) {
        final CheckBox checkbox = (CheckBox) view.findViewById(R.id.detail_starred);
        checkbox.setChecked(station.isStarred());
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeStation(checkbox.isChecked(), position);
            }
        });
    }

    public void changeStation(boolean star, int position) {
        Station station = stations.get(position);
        station.setStarred(star);

        if (stationUpdateDelegate != null) {
            stationUpdateDelegate.update(station);
        }

        if (!readOnly && position < stations.size()) {
            stations.remove(position);
            notifyDataSetChanged();
        }

    }

    private void handleStationsTextInfos(View view, Station station, StationPreferences stationPreferences) {
        TextView name = (TextView) view.findViewById(R.id.station_name);
        name.setText(station.getName(stationPreferences.isIdVisible()));

        if (stationPreferences.isUpdatedAtVisible()) {
            TextView lastUpdate = (TextView) view.findViewById(R.id.station_lastupdate);
            lastUpdate.setText(station.getLastUpdateAsString(resources));
        }

        TextView address = (TextView) view.findViewById(R.id.station_adress);
        address.setText(station.getAdressToUpperCase());

        TextView nbBikes = (TextView) view.findViewById(R.id.details_bikes);
        nbBikes.setText(station.getBikesAsString());
        nbBikes.setTextColor(getColor(station.getBikes()));

        TextView nbAttachs = (TextView) view.findViewById(R.id.details_attachs);
        nbAttachs.setText(station.getAttachsAsString());
        nbAttachs.setTextColor(getColor(station.getAttachs()));

        ViewUtils.switchView(view.findViewById(R.id.station_out_of_service_box), station.isOutOfService());
        ViewUtils.switchView(view.findViewById(R.id.details_cb), station.isCbPaiement());
        ViewUtils.switchView(view.findViewById(R.id.details_express), station.isExpress());
    }

    private int getColor(int number) {
        return ColorSelector.getColor(getContext(), number);
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d(TAG, "Dataset has changed!");
        Log.d(TAG, "Datasource " + stations.size());

        super.notifyDataSetChanged();
    }

    public void setStationUpdateDelegate(StationUpdateDelegate stationUpdateDelegate) {
        this.stationUpdateDelegate = stationUpdateDelegate;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
