package com.vlille.checker.ui.osm.overlay;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.model.Station;

/**
 * A customized InfoWindow handling star or unstar displayed station.
 */
public class BubbleInfoWindow extends DefaultInfoWindow {

	private ExtendedOverlayItem selectedItem;

	public BubbleInfoWindow(int layoutResId, final MapView mapView) {
		super(layoutResId, mapView);
		Button bubbleCheckbox = (Button) (mView.findViewById(R.id.maps_bubble_checkbox));
		bubbleCheckbox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Station station = (Station) selectedItem.getRelatedObject();
				station.setStarred(!station.isStarred());
				VlilleChecker.getDbAdapter().star(station.isStarred(), station);
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
	}

}
