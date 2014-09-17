package com.vlille.checker.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.delegate.StationUpdateDelegate;
import com.vlille.checker.ui.listener.MapTabListener;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.MapsIntentChooser;
import com.vlille.checker.utils.TextPlural;
import com.vlille.checker.utils.ViewUtils;
import com.vlille.checker.utils.color.ColorSelector;

import org.osmdroid.util.GeoPoint;

import java.util.List;
/**
 * A generic adapter for a stations ListView.
 */
public class StationsAdapter extends ArrayAdapter<Station> {

	private  static final String TAG = StationsAdapter.class.getSimpleName();

	private Activity activity;
    private StationUpdateDelegate stationUpdateDelegate;
    private List<Station> stations;
    private Resources resources;
    private boolean readOnly = false;

	public StationsAdapter(Context context, int resource, List<Station> stations) {
		super(context, resource, stations);

		this.activity = (Activity) context;
		this.stations = stations;
		this.resources = context.getResources();
	}

	@Override
	public View getView(final int position, View view, final ViewGroup parent) {
		if (view == null) {
			LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.station_list_item, null);
        }

        setStationAddressVisibility(view);
        setStationDetails(view, position);

		return view;
	}

    private void setStationAddressVisibility(View view) {
        ViewUtils.switchView(view.findViewById(R.id.station_adress_box), ContextHelper.isStationAddressVisible(getContext()));
    }

    /**
	 * Handle stations details.
	 */
	private void setStationDetails(View view, final int position) {
        final Station station = stations.get(position);
        ViewUtils.switchView(view.findViewById(R.id.station_actions), station.isSelected());

        handleStarCheckbox(view, position, station);

        boolean lastUpdateVisible = ContextHelper.isStationLastUpdateMomentVisible(getContext());
        handleStationsTextInfos(view, station, lastUpdateVisible);

        ViewUtils.switchView(view.findViewById(R.id.station_lastupdate), lastUpdateVisible);
        handleToMapButton(view, station);
        handleToNavigationButton(view, station);
	}

    private void handleToNavigationButton(View view, final Station station) {
        view.findViewById(R.id.station_action_tonavigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsIntentChooser.chooseIntent(activity, station);
            }
        });

    }

    private void handleStarCheckbox(View view, final int position, final Station station) {
        final CheckBox checkbox = (CheckBox) view.findViewById(R.id.detail_starred);
        checkbox.setChecked(station.isStarred());

        final ArrayAdapter<Station> arrayAdapter = this;
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                station.setStarred(checkbox.isChecked());
                if (stationUpdateDelegate != null) {
                    stationUpdateDelegate.update(station);
                }

                if (!readOnly && position < stations.size()) {
                    synchronized (stations) {
                        stations.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void handleStationsTextInfos(View view, Station station, boolean lastUpdateVisible) {
        TextView name = (TextView) view.findViewById(R.id.station_name);
        name.setText(station.getName());

        if (lastUpdateVisible) {
            String timeUnitSecond = TextPlural.toPlural(
                    station.getLastUpdate(),
                    resources.getString(R.string.timeunit_second));
            TextView lastUpdate = (TextView) view.findViewById(R.id.station_lastupdate);
            lastUpdate.setText(resources.getString(R.string.update_ago, station.getLastUpdate(), timeUnitSecond));
        }

        TextView address = (TextView) view.findViewById(R.id.station_adress);
        address.setText(station.getAdressToUpperCase());

        TextView nbBikes = (TextView) view.findViewById(R.id.details_bikes);
        nbBikes.setText(station.getBikesAsString());
        nbBikes.setTextColor(getColor(station.getBikes()));

        TextView nbAttachs = (TextView) view.findViewById(R.id.details_attachs);
        nbAttachs.setText(station.getAttachsAsString());
        nbAttachs.setTextColor(getColor(station.getAttachs()));

        LinearLayout boxOutOfService = (LinearLayout) view.findViewById(R.id.station_out_of_service_box);
        ViewUtils.switchView(boxOutOfService, station.isOutOfService());

        ImageView ccPaymentAllowed = (ImageView) view.findViewById(R.id.details_cb);
        ViewUtils.switchView(ccPaymentAllowed, station.isCbPaiement());
    }

    private void handleToMapButton(View view, final Station station) {
        ImageButton buttonToMap = (ImageButton) view.findViewById(R.id.station_action_tomap);
        buttonToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GeoPoint geoPoint = station.getGeoPoint();

                // Select the map tab and resets the tabListener to focus on selected station geoPoint.
                ActionBarActivity actionBarActivity = (ActionBarActivity) activity;
                MapTabListener mapTabListener = new MapTabListener(actionBarActivity, geoPoint);

                ActionBar.Tab mapTab = actionBarActivity.getSupportActionBar().getTabAt(2);
                mapTab.setTabListener(mapTabListener);
                mapTab.select();
            }
        });
    }

    private int getColor(int number) {
		return resources.getColor(ColorSelector.getColor(number));
	}

	@Override
	public void notifyDataSetChanged() {
		Log.d(TAG, "Dataset has changed!");

		super.notifyDataSetChanged();
	}

    public void setStationUpdateDelegate(StationUpdateDelegate stationUpdateDelegate) {
        this.stationUpdateDelegate = stationUpdateDelegate;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
