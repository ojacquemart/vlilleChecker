package com.vlille.checker.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;

import java.util.List;

public class SelectableStationAdapter extends ArrayAdapter<Station> {

    private List<Station> stations;

    public SelectableStationAdapter(Context context, int resource, List<Station> stations) {
        super(context, resource, stations);

        this.stations = stations;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        if (view == null) {
            LayoutInflater layout = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layout.inflate(R.layout.widget_station_list_item, null);
        }

        Station station = stations.get(position);

        CheckBox star = (CheckBox) view.findViewById(R.id.station_star);
        star.setChecked(station.isStarred());
        TextView name = (TextView) view.findViewById(R.id.station_name);
        name.setText(station.getName());

        return view;
    }

}
