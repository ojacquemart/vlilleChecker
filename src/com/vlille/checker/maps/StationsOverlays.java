package com.vlille.checker.maps;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ApplicationContextHelper;
import com.vlille.checker.utils.TextColorUtils;

public class StationsOverlays extends BalloonItemizedOverlay<StationsOverlays.MyOverlayItem> {
	
	private static final long PAINT_TEXT_SIZE = 15L;
	private static final int ONE_MINUTE_IN_MILLSECONDS = 1000 * 60;
	
	private final String LOG_TAG = getClass().getSimpleName();
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
		paint.setTextSize(PAINT_TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.LEFT);
		
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
		private Long lastUpdated = 0L;
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
			
			final String stationLastUpdated = detailledStation.getLastUpdated();
			if (!StringUtils.isEmpty(stationLastUpdated)) {
				final Long valueOfLastUpdated = Long.valueOf( stationLastUpdated.replaceAll("[^\\d]", "").trim());
				if (valueOfLastUpdated != null) {
					Log.d(LOG_TAG, "lastUpdated " + valueOfLastUpdated);
					this.lastUpdated = System.currentTimeMillis() + valueOfLastUpdated;
				}
			}
		}

		public Drawable getMarker(int stateBitset) {
			Drawable drawable = drawableMarker;
			try {
				if (marker && !displayOnlyPin) {
					drawableMarker.bikes = bikes;
					drawableMarker.attachs = attachs;
				} else if (station != null && ApplicationContextHelper.isStarred(mContext, station.getId())) {
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
		
		/**
		 * Check the last update.
		 * 
		 * @return boolean the station is up to date.
		 */
		public boolean isUpToDate() {
			boolean upToDate = false;
			
			if (lastUpdated != null) {
				long now = System.currentTimeMillis();
				upToDate = lastUpdated - (now - ONE_MINUTE_IN_MILLSECONDS) + ONE_MINUTE_IN_MILLSECONDS > 0;
				if (!upToDate) {
					// Update update time.
					lastUpdated = now;
				}
			}
			
			return upToDate;
		}

		public void setBikes(Integer bikes) {
			this.bikes = bikes;
		}

		public void setAttachs(Integer attachs) {
			this.attachs = attachs;
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
			
			paintBikes.setColor(TextColorUtils.getColorFromHexa(bikes));
			canvas.drawText(bikes.toString(), -25, -45, paintBikes);
			
			paintAttachs.setColor(TextColorUtils.getColorFromHexa(attachs));
			canvas.drawText(attachs.toString(), -25, -20, paintAttachs);
		}
	}
	
}
