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
import com.vlille.checker.stations.ColorSelector;

public class StationsOverlays extends BalloonItemizedOverlay<StationsOverlays.MyOverlayItem> {
	
	private final String LOG_TAG = getClass().getSimpleName();
	
	private final Resources resources;
	private float scaledDensity;
	private boolean marker;

	private final StationMarker drawableMarker;
	private final int mDrawableMarkerHeight;
	private final Drawable drawablePin;
	private final Drawable drawablePinStar;
	private final int mDrawableMarkerPinHeight;
	
	private volatile Paint paintBikes;
	private volatile Paint paintAttachs;

	public StationsOverlays(Drawable defaultMarker, VlilleMapView mapView, Context context) {
		super(defaultMarker, mapView, context, new ArrayList<MyOverlayItem>());

		resources = context.getResources();
		scaledDensity = resources.getDisplayMetrics().scaledDensity;
		
		drawableMarker = new StationMarker(context.getResources(), ((BitmapDrawable) defaultMarker).getBitmap());
		boundCenter(defaultMarker);
		
		mDrawableMarkerHeight = drawableMarker.getIntrinsicHeight();
		Log.d(LOG_TAG, "overlay image height = " + mDrawableMarkerHeight);
		
		drawablePin = context.getResources().getDrawable(R.drawable.station_pin);
		drawablePinStar = context.getResources().getDrawable(R.drawable.station_pin_star);
		mDrawableMarkerPinHeight = drawablePinStar.getIntrinsicHeight();

		paintBikes = initPaint();
		paintAttachs = initPaint();
	}

	private Paint initPaint() {
		Paint paint = new Paint();
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(resources.getDimensionPixelSize(R.dimen.overlay_font_size));
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		return paint;
	}

	public void addOverlay(MyOverlayItem station) {
		mStationsOverlay.add(station);
	}
	
	public synchronized void populateNow() {
		populate();
	}
	
	@Override
	protected synchronized MyOverlayItem createItem(int i) {
		return mStationsOverlay.get(i);
	}

	@Override
	public synchronized  int size() {
		return mStationsOverlay.size();
	}
	
	public int getDrawableMarkerHeight() {
		return mDrawableMarkerHeight;
	}

	@Override
	public synchronized void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
		setLastFocusedIndex(-1);
		
		if (!shadow) {
			marker = VlilleMapView.isDetailledZoomLevel(mapView.getZoomLevel());
			setBalloonBottomOffset(marker ? mDrawableMarkerHeight : mDrawableMarkerPinHeight);
		}

		populate();
		
		super.draw(canvas, mapView, false);
	}
	
	public List<MyOverlayItem> getStationsOverlay() {
		return mStationsOverlay;
	}

	/**
	 * Overlay for customize the ballon.
	 */
	public class MyOverlayItem extends OverlayItem {
		
		private Station station; /** Station id for load station details (bikes and attachs). */
		private Integer bikes = 0;
		private Integer attachs = 0;
		private boolean displayOnlyPin = false;
		
		public MyOverlayItem(GeoPoint point) {
			super(point, null, null);
			this.displayOnlyPin = true;
		}

		public void copyDetailledStation(Station detailledStation) {
			this.bikes = detailledStation.getBikes();
			this.attachs = detailledStation.getAttachs();
		}

		public Drawable getMarker(int stateBitset) {
			Drawable drawable = drawableMarker;
			try {
				if (marker && !displayOnlyPin) {
					drawableMarker.bikes = bikes;
					drawableMarker.attachs = attachs;
				} else if (station != null && VlilleChecker.getDbAdapter().isStarred(station)) {
					drawable = drawablePinStar;
				} else {
					drawable = drawablePin;
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "#getMarker exception", e);
			}
			
			drawable.setBounds(
				- drawable.getIntrinsicWidth() / 2,
				- drawable.getIntrinsicHeight(),
				drawable.getIntrinsicWidth() / 2, 0);

			return drawable;
		}

		public void updateMarker(boolean onylyPin) {
			this.displayOnlyPin = onylyPin;
			this.setMarker(getMarker(0));
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
			
			paintBikes.setColor(resources.getColor(ColorSelector.getColor(bikes)));
			canvas.drawText(bikes.toString(), x, -30 * scaledDensity, paintBikes);
			
			paintAttachs.setColor(resources.getColor(ColorSelector.getColor(attachs)));
			canvas.drawText(attachs.toString(), x, -14 * scaledDensity, paintAttachs);
		}
	}
	
}
