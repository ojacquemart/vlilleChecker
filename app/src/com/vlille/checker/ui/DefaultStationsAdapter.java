package com.vlille.checker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.listener.MapTabListener;
import com.vlille.checker.utils.ColorSelector;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.TextUtils;
import com.vlille.checker.utils.ViewUtils;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Adapter for the stars stations detail.
 */
public class DefaultStationsAdapter extends ArrayAdapter<Station> {

	private  static final String TAG = DefaultStationsAdapter.class.getSimpleName();

	private Activity activity;
    private List<Station> stations;
    private Resources resources;
    private boolean readOnly = false;

	public DefaultStationsAdapter(Context context, int resource, List<Station> stations) {
		super(context, resource, stations);

		this.activity = (Activity) context;
		this.stations = stations;
		this.resources = context.getResources();
	}

	@Override
	public View getView(final int position, View view, final ViewGroup parent) {
		if (view == null) {
			LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.stars_list_content, null);
			
            ViewUtils.switchView(view.findViewById(R.id.station_adress_box), ContextHelper.isDisplayingStationAdress(getContext()));
		}

        handleStationDetails(view, position);

		return view;
	}

	/**
	 * Handle stations details.
	 */
	private void handleStationDetails(View view, final int position) {
        final Station station = stations.get(position);

        ViewUtils.switchView(view.findViewById(R.id.station_actions), station.isSelected());

        handleStarCheckbox(view, position, station);
        handleStationsTextInfos(view, station);
        handleToMapButton(view, station);
	}

    private void handleStarCheckbox(View view, final int position, final Station station) {
        final CheckBox checkbox = (CheckBox) view.findViewById(R.id.detail_starred);
        checkbox.setChecked(station.isStarred());

        final ArrayAdapter<Station> arrayAdapter = this;
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VlilleChecker.getDbAdapter().star(checkbox.isChecked(), station);
                station.setStarred(checkbox.isChecked());

                if (!readOnly) {
                    stations.remove(position);
                    arrayAdapter.notifyDataSetChanged();

                    if (stations.isEmpty()) {
                        activity.findViewById(R.id.home_nostations_nfo).setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void handleStationsTextInfos(View view, Station station) {
        TextView name = (TextView) view.findViewById(R.id.station_name);
        name.setText(station.getName());

        String timeUnitSecond = TextUtils.formatPlural(
                station.getLastUpdate(),
                resources.getString(R.string.timeunit_second));
        TextView lastUpdate = (TextView) view.findViewById(R.id.station_lastupdate);
        lastUpdate.setText(resources.getString(R.string.update_ago, station.getLastUpdate(), timeUnitSecond));

        TextView address = (TextView) view.findViewById(R.id.station_adress);
        address.setText(TextUtils.toCamelCase(station.getAdress()));

        TextView nbBikes = (TextView) view.findViewById(R.id.details_bikes);
        nbBikes.setText(station.getStringBikes());
        nbBikes.setTextColor(getColor(station.getBikes()));

        TextView nbAttachs = (TextView) view.findViewById(R.id.details_attachs);
        nbAttachs.setText(station.getStringAttachs());
        nbAttachs.setTextColor(getColor(station.getAttachs()));

        LinearLayout boxOutOfService = (LinearLayout) view.findViewById(R.id.station_out_of_service_box);
        ViewUtils.switchView(boxOutOfService, station.isOutOfService());

        ImageView ccPaymentAllowed = (ImageView) view.findViewById(R.id.details_cb);
        ccPaymentAllowed.setBackgroundResource(R.drawable.station_cb);
        ViewUtils.switchView(ccPaymentAllowed, station.isCbPaiement());
    }

    private void handleToMapButton(View view, final Station station) {
        ImageButton buttonToMap = (ImageButton) view.findViewById(R.id.station_action_tomap);
        buttonToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint geoPoint = station.getGeoPoint();

                // Select the map tab and resets the tabListener to focus on selected station geoPoint.
                SherlockFragmentActivity sherlockFragmentActivity = (SherlockFragmentActivity) activity;
                MapTabListener mapTabListener = new MapTabListener(sherlockFragmentActivity, geoPoint);

                Tab mapTab = sherlockFragmentActivity.getSupportActionBar().getTabAt(2);
                mapTab.setTabListener(mapTabListener);
                mapTab.select();
            }
        });
    }

    private int getColor(int number) {
		return resources.getColor(ColorSelector.getColor(number, false));
	}

	@Override
	public void notifyDataSetChanged() {
		Log.d(TAG, "Dataset has changed!");

		super.notifyDataSetChanged();
	}

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}
