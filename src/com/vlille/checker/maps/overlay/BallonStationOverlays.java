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
import com.vlille.checker.maps.overlay.BallonStationOverlays.StationDetails;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ColorSelector;

/**
 * Store every stations overlays.
 */
public class BallonStationOverlays extends BalloonItemizedOverlay<StationDetails> {
	
	private static final long serialVersionUID = 1L;
	
	private final String LOG_TAG = getClass().getSimpleName();
	
	private float scaledDensity;

	public static boolean detailledZoomLevel;
	public static StationMarker drawableMarker;
	public static Drawable drawablePin;
	public static Drawable drawablePinStar;
	
	private final int drawableMarkerHeight;
	private final int drawableMarkerPinHeight;
	
	private final Resources resources;
	private volatile Paint paint;
	
	public BallonStationOverlays(Drawable defaultMarker, VlilleMapView mapView, Context context) {
		super(defaultMarker, mapView, context, new ArrayList<StationDetails>(), VlilleChecker.getDbAdapter().getStarredStations());

		resources = context.getResources();
		scaledDensity = resources.getDisplayMetrics().scaledDensity;
		
		drawableMarker = new StationMarker(resources, ((BitmapDrawable) defaultMarker).getBitmap());
		boundCenter(defaultMarker);
		
		drawableMarkerHeight = drawableMarker.getIntrinsicHeight();
		Log.d(LOG_TAG, "overlay image height = " + drawableMarkerHeight);
		
		drawablePin = resources.getDrawable(R.drawable.station_pin);
		drawablePinStar = resources.getDrawable(R.drawable.station_pin_star);
		drawableMarkerPinHeight = drawablePinStar.getIntrinsicHeight();

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

	public void addOverlay(StationDetails station) {
		detailsOverlays.add(station);
	}
	
	@Override
	public StationDetails createItem(int i) {
		return detailsOverlays.get(i);
	}

	@Override
	public synchronized int size() {
		return detailsOverlays.size();
	}
	
	public synchronized int getDrawableMarkerHeight() {
		return drawableMarkerHeight;
	}
	
	public synchronized void populateNow() {
		populate();
	}

	/**
	 * First, all overlays are drawn with shadow at true, then at false...
	 */
	@Override
	public synchronized void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow) {
			return;
		}
		
		detailledZoomLevel = VlilleMapView.isDetailledZoomLevel(mapView.getZoomLevel());
		
		setLastFocusedIndex(-1);
		setBalloonBottomOffset(detailledZoomLevel ? drawableMarkerHeight : drawableMarkerPinHeight);
		
		populate();
		
		super.draw(canvas, mapView, false);
	}
	
	public synchronized List<StationDetails> getStationsOverlay() {
		return detailsOverlays;
	}

	public StationDetails createNewOverlay(GeoPoint geoPoint, Station station) {
		final StationDetails overlay = new StationDetails(geoPoint, station);
		addOverlay(overlay);
		
		return overlay;
	}
	
	public class StationDetails extends OverlayItem {

		private final String LOG_TAG = getClass().getSimpleName();

		private Station station;
		private Integer bikes = 0;
		private Integer attachs = 0;
		private Boolean isMarkerPin = false;
		
		public StationDetails(GeoPoint geoPoint, Station station) {
			this(geoPoint);
			
			this.station = station;
		}

		public StationDetails(GeoPoint point) {
			super(point, null, null);
			this.isMarkerPin = false;
		}
		
		public void copyDetailledStation(Station detailledStation) {
			this.bikes = detailledStation.getBikes();
			this.attachs = detailledStation.getAttachs();
		}

		/**
		 * Get marker is called twice. First by #draw with shadow=true, then by shadow=false. WARN: add log really slows the
		 * maps.
		 */
		@Override
		public synchronized Drawable getMarker(int stateBitset) {
			Drawable drawable = null;
			try {
				if (!isMarkerPin && BallonStationOverlays.detailledZoomLevel) {
					drawableMarker.bikes = this.bikes;
					drawableMarker.attachs = this.attachs;
					drawable = drawableMarker;
				} else if (station != null && isStarred(station)) {
					drawable = BallonStationOverlays.drawablePinStar;
				} else {
					drawable = BallonStationOverlays.drawablePin;
				}

				drawable.setBounds(
						-drawable.getIntrinsicWidth() / 2,
						-drawable.getIntrinsicHeight(),
						drawable.getIntrinsicWidth() / 2,
						0);
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

		@Override
		public boolean equals(Object o) {
			StationDetails other = (StationDetails) o;
			
			return station.equals(other.getStation());
		}

		
		@Override
		public int hashCode() {
			return station.hashCode();
		}	

	}	

	public class StationMarker extends BitmapDrawable {
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
