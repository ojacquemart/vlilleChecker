package com.vlille.checker.maps.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.maps.VlilleMapView;
import com.vlille.checker.maps.overlay.StationsOverlays.MyOverlayItem;
import com.vlille.checker.model.Station;

/**
 * An abstract extension of ItemizedOverlay for displaying an information
 * balloon upon screen-tap of each marker overlay.
 * 
 * @author Jeff Gilfelt
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends
		ItemizedOverlay<Item> {

	private static final String LOG_TAG = "BalloonItemizedOverlay";
	private static final DbAdapter DB_ADAPTER = VlilleChecker.getDbAdapter();
	
	private VlilleMapView mMapView;
	private BalloonOverlayView<OverlayItem> balloonView;
	/*private View clickRegion;*/
	private int viewOffset;
	final MapController mc;

	protected Context mContext;
	protected List<MyOverlayItem> mStationsOverlay;
	
	private List<Station> starredStations = new ArrayList<Station>();


	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker
	 *            - A bounded Drawable to be drawn on the map for each item in
	 *            the overlay.
	 * @param mapView
	 *            - The view upon which the overlay items are to be drawn.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, VlilleMapView mapView,
			Context context, List<MyOverlayItem> stations, List<Station> starredStations) {
		super(defaultMarker);
		
		this.mMapView = mapView;
		this.mStationsOverlay = stations;
		this.viewOffset = 0;
		this.mc = mapView.getController();
		this.mContext = context;
		this.starredStations = starredStations;
	}
	
	/**
	 * Set the horizontal distance between the marker and the bottom of the
	 * information balloon. The default is 0 which works well for center bounded
	 * markers. If your marker is center-bottom bounded, call this before adding
	 * overlay items to ensure the balloon hovers exactly above the marker.
	 * 
	 * @param pixels
	 *            - The padding between the center point and the bottom of the
	 *            information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		if (balloonView != null) {
			balloonView.setBalloonBottomOffset(pixels);
		} else {
			viewOffset = pixels;
		}
	}

	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	protected boolean onBalloonTap(int index, Item item) {
		return true;
	}
	
	public boolean isStarred(Station station) {
		return starredStations.contains(station);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected boolean onTap(int index) {
		Log.d(LOG_TAG, "overlay onTap");
		MyOverlayItem item = mStationsOverlay.get(index);
		final Station station = item.getStation();
		
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = new BalloonOverlayView<OverlayItem>(mContext, 0);
			balloonView.getFavoriteCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
					DB_ADAPTER.star(checked, station);
					station.setStarred(checked);
					
					if (checked) {
						starredStations.add(station);
					} else {
						starredStations.remove(station);
					}
				}
			});
			isRecycled = false;
		}  else {
			isRecycled = true;
		}
		
		GeoPoint point = item.getPoint();
		
		balloonView.setVisibility(View.GONE);
		balloonView.setData(item, null);
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
			// Needed to resize if content is smaller. But why ? May be a bug
			//TODO : check if it's not because a fill parent instead a wrap content
			balloonView.measure(
					View.MeasureSpec.EXACTLY,
					View.MeasureSpec.EXACTLY);
		} else {
			mMapView.addView(balloonView, params);
			
			@SuppressWarnings("unchecked")
			Item castedItem = (Item) item;
			setFocus(castedItem);
		}
		
		mMapView.animateToAndUpdateOverlays(item.getPoint());

		return true;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		boolean tapped = super.onTap(p, mapView);
		if (!tapped) {
			hideBalloon();
		}
		
		return tapped;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that can
	 * populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(),
				viewOffset);
	}

	/**
	 * Expose map view to subclasses. Helps with creation of balloon views.
	 */
	protected MapView getMapView() {
		return mMapView;
	}

	/**
	 * Sets the visibility of this overlay's balloon view to GONE.
	 */
	public void hideBalloon() {
		if (balloonView == null)
			return;
		balloonView.setVisibility(View.GONE);
	}

	public boolean isBalloonShowing() {
		if (balloonView == null)
			return false;
		return balloonView.getVisibility() == View.VISIBLE;
	}

}
