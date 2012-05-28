package com.vlille.checker.activity;

import java.util.List;

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
import com.vlille.checker.utils.MiscUtils;

/**
 * Adapter for the stations detail.
 */
public class HomeAdapter extends ArrayAdapter<Station> {

	private final String LOG_TAG = getClass().getSimpleName();

	private List<Station> stations; /** The stations loaded. */
	private Resources resources; /** Resources for the color text according to station informations. */
	private LinearLayout boxAddStation; /** The box containing the add button. */

	public HomeAdapter(Context context, int resource, List<Station> stations, LinearLayout boxAddStation) {
		super(context, resource, stations);

		this.stations = stations;
		this.resources = context.getResources();
		this.boxAddStation = boxAddStation;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.home_list_stations, null);
			
			// Hide or display the adress box.
			final boolean displayStationAdress = ContextHelper.isDisplayingStationAdress(getContext());
			final View stationAdressBox = view.findViewById(R.id.station_adress_box);
			stationAdressBox.setVisibility(displayStationAdress ? LinearLayout.VISIBLE : LinearLayout.GONE);
		}
		
		if (position < 0) {
			return view;
		}

		final Station station = stations.get(position);
		final ArrayAdapter<Station> arrayAdapter = (ArrayAdapter<Station>) this;
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.detail_starred);
		checkbox.setChecked(true);
		checkbox.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stations.remove(position);
				VlilleChecker.getDbAdapter().star(false, station);
				arrayAdapter.notifyDataSetChanged();
			}
		});

		if (station != null) {
			handleStationDetails(view, station);
		}

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
		
		TextView adress = (TextView) view.findViewById(R.id.station_adress);
		adress.setText(MiscUtils.toCamelCase(station.getAdress()));
		
		TextView nbBikes = (TextView) view.findViewById(R.id.details_bikes);
		nbBikes.setText(station.getBikes().toString());
		nbBikes.setTextColor(getColor(station.getBikes()));
		
		TextView nbAttachs = (TextView) view.findViewById(R.id.details_attachs);
		nbAttachs.setText(station.getAttachs().toString());
		nbAttachs.setTextColor(getColor(station.getAttachs()));

		LinearLayout boxOutOfService = (LinearLayout) view.findViewById(R.id.station_out_of_service_box);
		MiscUtils.showOrMask(boxOutOfService, station.isOutOfService());

		ImageView cbPaiementAllowed = (ImageView) view.findViewById(R.id.details_cb);
		cbPaiementAllowed.setBackgroundResource(station.isCbPaiement() ? R.drawable.station_cb : R.drawable.station_nocb);
	}
	
	private int getColor(int number) {
		return resources.getColor(ColorSelector.getColor(number, false));
	}

	@Override
	public void notifyDataSetChanged() {
		Log.d(LOG_TAG, "Station deleted");
		MiscUtils.showOrMask(boxAddStation, stations.isEmpty());

		super.notifyDataSetChanged();
	}

}
