package com.vlille.checker.ui.osm.overlay.window;

import org.osmdroid.views.MapView;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.StationUpdateDelegate;
import com.vlille.checker.ui.async.AbstractStationsAsyncTask;
import com.vlille.checker.ui.osm.overlay.ExtendedOverlayItem;
import com.vlille.checker.ui.osm.overlay.MaskableOverlayItem;
import com.vlille.checker.ui.osm.overlay.OverlayZoomUtils;
import com.vlille.checker.utils.ViewUtils;
import com.vlille.checker.utils.color.ColorSelector;

import java.util.Arrays;
import java.util.List;

/**
 * A customized InfoWindow handling star or unstar displayed station.
 */
public class BubbleInfoWindow extends DefaultInfoWindow {

    private ExtendedOverlayItem selectedItem;
    private TextView stationBikes;
    private TextView stationAttachs;

    private SherlockFragmentActivity sherlockActivity;
    private StationUpdateDelegate stationUpdateDelegate;

    public BubbleInfoWindow(MapView mapView,
                            SherlockFragmentActivity sherlockActivity,
                            final StationUpdateDelegate stationUpdateDelegate) {
		super(R.layout.maps_bubble, mapView);
        this.sherlockActivity = sherlockActivity;
        this.stationUpdateDelegate = stationUpdateDelegate;

        Button bubbleCheckbox = (Button) (mView.findViewById(R.id.maps_bubble_checkbox));
        bubbleCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Station station = (Station) selectedItem.getRelatedObject();
                station.setStarred(!station.isStarred());

                stationUpdateDelegate.update(station);
            }
        });
    }
	
	/**
	 * open the window at the specified position. 
	 * @param item the item on which is hooked the view
	 * @param offsetX (&offsetY) the offset of the view to the position, in pixels. 
	 * This allows to offset the view from the marker position. 
	 */
	@Override
	public void open(ExtendedOverlayItem item, int offsetX, int offsetY) {
		MaskableOverlayItem maskableItem = (MaskableOverlayItem) item;
		if (maskableItem.isVisible()) {
			super.open(item, offsetX, offsetY);
		}
	}

	@Override
	public void onOpen(ExtendedOverlayItem item) {
		selectedItem = item;
		
		String title = item.getTitle();
		if (title == null) {
			title = "";
		}
		((TextView) mView.findViewById(R.id.maps_bubble_title)).setText(title);

		Station station = (Station) item.getRelatedObject();
		((CheckBox) mView.findViewById(R.id.maps_bubble_checkbox)).setChecked(station.isStarred());

        boolean detailledZoomLevel = OverlayZoomUtils.isDetailledZoomLevel(getZoomLevel());
        ViewUtils.switchView(mView.findViewById(R.id.maps_bubble_station_table), !detailledZoomLevel);
        if (!detailledZoomLevel) {
            stationBikes = (TextView) mView.findViewById(R.id.maps_bubble_station_bikes);
            stationAttachs = (TextView) mView.findViewById(R.id.maps_bubble_station_attachs);

            // Bind station with old values before async refresh.
            bindStation(station);

            SingleStationAsyncTask asyncTask = new SingleStationAsyncTask();
            asyncTask.setDelegate(stationUpdateDelegate);
            asyncTask.execute(Arrays.asList(station));
        }
	}

    private void bindStation(Station station) {
        updateTextView(stationBikes, station.getBikesAsString(), ColorSelector.getColorForMap(station.getBikes()));
        updateTextView(stationAttachs, station.getAttachsAsString(), ColorSelector.getColorForMap(station.getAttachs()));
    }
    private void updateTextView(TextView textView, String text, int color) {
        textView.setText(text);
        textView.setTextColor(mView.getResources().getColor(color));
    }

    class SingleStationAsyncTask extends AbstractStationsAsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(List<Station> stations) {
            super.onPostExecute(stations);
            if (stations != null && stations.size() == 1) {
                Station station = stations.get(0);
                bindStation(station);
            }

            setSupportProgressBarIndeterminateVisibility(false);
        }

        private void setSupportProgressBarIndeterminateVisibility(boolean visible) {
            if (sherlockActivity != null) {
                sherlockActivity.setSupportProgressBarIndeterminateVisibility(visible);
            }
        }
    }

}
