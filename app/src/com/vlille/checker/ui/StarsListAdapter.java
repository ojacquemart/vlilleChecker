package com.vlille.checker.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ColorSelector;
import com.vlille.checker.utils.ContextHelper;
import com.vlille.checker.utils.TextUtils;
import com.vlille.checker.utils.ViewUtils;

/**
 * Adapter for the stars stations detail.
 */
public class StarsListAdapter extends ArrayAdapter<Station> {

	private final String LOG_TAG = getClass().getSimpleName();

    public boolean readOnly = false;

    /**
     * The currenct activity.
     */
	private Activity activity;
    /** The stations loaded. */
	private List<Station> stations;
    /** Resources for the color text according to station informations. */
	private Resources resources;

	public StarsListAdapter(Context context, int resource, List<Station> stations) {
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
			
			// Hide or display the adress box.
			final boolean displayStationAdress = ContextHelper.isDisplayingStationAdress(getContext());
			final View stationAdressBox = view.findViewById(R.id.station_adress_box);
			stationAdressBox.setVisibility(displayStationAdress ? View.VISIBLE : View.GONE);
		}

		final Station station = stations.get(position);

		final CheckBox checkbox = (CheckBox) view.findViewById(R.id.detail_starred);
        checkbox.setChecked(station.isStarred());

        final ArrayAdapter<Station> arrayAdapter = this;
        checkbox.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VlilleChecker.getDbAdapter().star(checkbox.isChecked(), station);
                station.setStarred(checkbox.isChecked());

                if (!readOnly) {
                    stations.remove(position);
                    arrayAdapter.notifyDataSetChanged();

                    // Redisplay the home no stations info box.
                    // TODO: see to use VlilleSherlockListFragment#hide... method which does the same.
                    if (stations.isEmpty()) {
                        activity.findViewById(R.id.home_nostations_nfo).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        handleStationDetails(view, station);

		return view;
	}

	/**
	 * Handle stations details.
	 * 
	 * @param view
	 * @param station
	 */
	private void handleStationDetails(View view, final Station station) {
		TextView name = (TextView) view.findViewById(R.id.station_name);
		name.setText(station.getName());
		TextView lastUpdate = (TextView) view.findViewById(R.id.station_lastupdate);
		
		String timeUnitSecond = TextUtils.formatPlural(station.getLastUpdate(), resources.getString(R.string.timeunit_second));
		lastUpdate.setText(resources.getString(R.string.update_ago, station.getLastUpdate(), timeUnitSecond));
		
		TextView adress = (TextView) view.findViewById(R.id.station_adress);
		adress.setText(TextUtils.toCamelCase(station.getAdress()));
		
		TextView nbBikes = (TextView) view.findViewById(R.id.details_bikes);
		nbBikes.setText(station.getStringBikes());
		nbBikes.setTextColor(getColor(station.getBikes()));
		
		TextView nbAttachs = (TextView) view.findViewById(R.id.details_attachs);
		nbAttachs.setText(station.getStringAttachs());
		nbAttachs.setTextColor(getColor(station.getAttachs()));

		LinearLayout boxOutOfService = (LinearLayout) view.findViewById(R.id.station_out_of_service_box);
		ViewUtils.switchView(boxOutOfService, station.isOutOfService());

		ImageView cbPaiementAllowed = (ImageView) view.findViewById(R.id.details_cb);
		cbPaiementAllowed.setBackgroundResource(R.drawable.station_cb);
		ViewUtils.switchView(cbPaiementAllowed, station.isCbPaiement());
	}
	
	private int getColor(int number) {
		return resources.getColor(ColorSelector.getColor(number, false));
	}

	@Override
	public void notifyDataSetChanged() {
		Log.d(LOG_TAG, "Dataset changed!");

		super.notifyDataSetChanged();
	}

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
