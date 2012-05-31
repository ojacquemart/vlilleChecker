package com.vlille.checker.maps.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.vlille.checker.R;
import com.vlille.checker.VlilleChecker;
import com.vlille.checker.maps.VlilleMapView;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ColorSelector;

public class StationsOverlays extends BalloonItemizedOverlay<StationsOverlays.MyOverlayItem> {
	
	private final String LOG_TAG = getClass().getSimpleName();
	
	private float scaledDensity;
	private boolean detailledZoomLevel;

	private final StationMarker drawableMarker;
	private final int mDrawableMarkerHeight;
	private final Drawable drawablePin;
	private final Drawable drawablePinStar;
	private final int mDrawableMarkerPinHeight;
	
	private final Resources resources;
	private volatile Paint paint;
	
	public StationsOverlays(Drawable defaultMarker, VlilleMapView mapView, Context context) {
		super(defaultMarker, mapView, context, new ArrayList<MyOverlayItem>(), VlilleChecker.getDbAdapter().getStarredStations());

		resources = context.getResources();
		scaledDensity = resources.getDisplayMetrics().scaledDensity;
		
		drawableMarker = new StationMarker(resources, ((BitmapDrawable) defaultMarker).getBitmap());
		boundCenter(defaultMarker);
		
		mDrawableMarkerHeight = drawableMarker.getIntrinsicHeight();
		Log.d(LOG_TAG, "overlay image height = " + mDrawableMarkerHeight);
		
		drawablePin = resources.getDrawable(R.drawable.station_pin);
		drawablePinStar = resources.getDrawable(R.drawable.station_pin_star);
		mDrawableMarkerPinHeight = drawablePinStar.getIntrinsicHeight();

		paint = initPaint(resources.getDimensionPixelSize(R.dimen.overlay_font_size));
	}

	private Paint initPaint(int textSize) {
		Paint paint = new Paint();
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		return paint;
	}

	public void addOverlay(MyOverlayItem station) {
		mStationsOverlay.add(station);
	}
	
	@Override
	protected MyOverlayItem createItem(int i) {
		return mStationsOverlay.get(i);
	}

	@Override
	public int size() {
		return mStationsOverlay.size();
	}
	
	public int getDrawableMarkerHeight() {
		return mDrawableMarkerHeight;
	}
	
	public void populateNow() {
		populate();
	}

	/**
	 * First all overlays are drawn with shadow at true, then at false...
	 */
	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
		detailledZoomLevel = VlilleMapView.isDetailledZoomLevel(mapView.getZoomLevel());
		
		if (!shadow) {
			setLastFocusedIndex(-1);
			Log.d(LOG_TAG, "Detailled zoom level? " + detailledZoomLevel);
			setBalloonBottomOffset(detailledZoomLevel ? mDrawableMarkerHeight : mDrawableMarkerPinHeight);
		}
		
		populate();
		
		super.draw(canvas, mapView, false);
	}
	
	public synchronized List<MyOverlayItem> getStationsOverlay() {
		return mStationsOverlay;
	}

	/**
	 * Overlay for customize the ballon.
	 */
	public class MyOverlayItem extends OverlayItem {
		
		private Station station; /** Station id for load station details (bikes and attachs). */
		private Integer bikes = 0;
		private Integer attachs = 0;
		private boolean isMarkerPin = false;
		
		public MyOverlayItem(GeoPoint point) {
			super(point, null, null);
			this.isMarkerPin = false;
		}

		public void copyDetailledStation(Station detailledStation) {
			this.bikes = detailledStation.getBikes();
			this.attachs = detailledStation.getAttachs();
		}

		/**
		 * Get marker is called twice. First by #draw with shadow=true, then by shadow=false.
		 * WARN: add log really slows the maps.
		 */
		public Drawable getMarker(int stateBitset) {
			Drawable drawable = null;
			try {
				if (!isMarkerPin && detailledZoomLevel) {
					drawable = drawableMarker;
					drawableMarker.bikes = bikes;
					drawableMarker.attachs = attachs;
				} else if (station != null && isStarred(station)) {
					drawable = drawablePinStar;
				} else {
					drawable = drawablePin;
				}
				
				drawable.setBounds(
						- drawable.getIntrinsicWidth() / 2,
						- drawable.getIntrinsicHeight(),
						drawable.getIntrinsicWidth() / 2, 0);
			} catch (Exception e) {
				Log.e(LOG_TAG, "#getMarker exception", e);
			}
			
			return drawable;
		}

		public void setMarkerPin(boolean isMarkerPin) {
			this.isMarkerPin = isMarkerPin;
		}

		public Station getStation() {
			return station;
		}

		public void setStation(Station station) {
			this.station = station;
		}

	}

	private class StationMarker extends BitmapDrawable {
		public volatile Integer bikes = 0;
		public volatile Integer attachs = 0;

		public StationMarker(Resources r, Bitmap b) {
			super(r, b);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			
//			final float x = -18 * scaledDensity; // Align.LEFT
			final float x = -11 * scaledDensity; // Align.CENTER
			
			paint.setColor(resources.getColor(ColorSelector.getColor(bikes)));
			canvas.drawText(bikes.toString(), x, -30 * scaledDensity, paint);
			
			paint.setColor(resources.getColor(ColorSelector.getColor(attachs)));
			canvas.drawText(attachs.toString(), x, -14 * scaledDensity, paint);
		}
	}
	
}
