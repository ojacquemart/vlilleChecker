package com.vlille.checker.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;

public class SelectStationsAdapter extends ArrayAdapter<Station> {

	private List<Station> stations;
	
	public SelectStationsAdapter(Context context, int resource, List<Station> stations) {
		super(context, resource, stations);
		
		this.stations = stations;
	} 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		final Context context = getContext();
		if (view == null) {
			LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout.inflate(R.layout.home_search_list_stations, null);
		}
		
		final Station station = stations.get(position);
		
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.preferences_station_checked);
		TextView text = (TextView) view.findViewById(R.id.preferences_station_title);
		if (station != null && checkbox != null && text != null) {
			text.setText(station.getName());
			
			checkbox.setChecked(station.isStarred());
			checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					CheckBox onclickCheckbox = (CheckBox) v.findViewById(R.id.preferences_station_checked);
					
					VlilleChecker.getDbAdapter().star(onclickCheckbox.isChecked(), station);
				}
			});
				
		}
		
		return view;
	}

}
