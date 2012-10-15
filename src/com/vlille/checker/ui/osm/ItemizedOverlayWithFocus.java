package com.vlille.checker.ui.osm;

import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ColorSelector;

public class ItemizedOverlayWithFocus<Item extends OverlayItem> extends ItemizedIconOverlay<Item> {
	
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int DESCRIPTION_BOX_PADDING = 3;
	public static final int DESCRIPTION_BOX_CORNERWIDTH = 3;
	
	public static final int DESCRIPTION_LINE_HEIGHT = 12;
	/** Additional to <code>DESCRIPTION_LINE_HEIGHT</code>. */
	public static final int DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT = 2;

	// protected static final Point DEFAULTMARKER_FOCUSED_HOTSPOT = new Point(10, 19);
	protected static final int DEFAULTMARKER_BACKGROUNDCOLOR = Color.rgb(101, 185, 74);

	protected static final int DESCRIPTION_MAXWIDTH = 200;
	
	// ===========================================================
	// osmdroid bonus bubble
	// ===========================================================
	
	protected InfoWindow mBubble = null;
	protected OverlayItem mItemWithBubble = null;

	// ===========================================================
	// Fields
	// ===========================================================

	protected int mMarkerFocusedBackgroundColor;
	private Paint mDescriptionPaint, mTitlePaint;
	protected int mTextSize;	

	protected Drawable mMarkerFocused;
	protected Drawable mMarkerStarred;
	protected int mFocusedItemIndex;
	protected boolean mFocusItemsOnTap;

	// ===========================================================
	// Constructors
	// ==========================================================

	public ItemizedOverlayWithFocus(
			final List<Item> aList,
			final Resources resources,
			final InfoWindow pInfoWindow,
			final OnItemGestureListener<Item> aOnItemTapListener,
			final ResourceProxy pResourceProxy) {
		super(aList, resources.getDrawable(R.drawable.station_pin), aOnItemTapListener, pResourceProxy);
		
		this.mMarkerFocused = resources.getDrawable(R.drawable.station_marker);
		this.mMarkerStarred = resources.getDrawable(R.drawable.station_pin_star);
		this.mBubble = pInfoWindow;
		this.mTextSize = resources.getDimensionPixelSize(R.dimen.overlay_font_size);
		this.mMarkerFocusedBackgroundColor = DEFAULTMARKER_BACKGROUNDCOLOR;
		
		this.mDescriptionPaint = new Paint();
		this.mDescriptionPaint.setAntiAlias(true);
		this.initPaint();
			
		mItemWithBubble = null;
		this.unSetFocusedItem();
	}

	private void initPaint() {
		this.mTitlePaint = new Paint();
		this.mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		this.mTitlePaint.setAntiAlias(true);
		this.mTitlePaint.setTextAlign(Align.CENTER);
		this.mTitlePaint.setTextSize(mTextSize);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Item getFocusedItem() {
		if (this.mFocusedItemIndex == NOT_SET) {
			return null;
		}
		return this.mItemList.get(this.mFocusedItemIndex);
	}

	public void setFocusedItem(final int pIndex) {
		this.mFocusedItemIndex = pIndex;
	}

	public void unSetFocusedItem() {
		this.mFocusedItemIndex = NOT_SET;
	}

	public void setFocusedItem(final Item pItem) {
		final int indexFound = super.mItemList.indexOf(pItem);
		if (indexFound < 0) {
			throw new IllegalArgumentException();
		}

		this.setFocusedItem(indexFound);
	}

	public void setFocusItemsOnTap(final boolean doit) {
		this.mFocusItemsOnTap = doit;
	}

	// ===========================================================
	// Methods from for draw bubble
	// ===========================================================
	
	/**
	 * Opens the bubble on the item. 
	 * For each ItemizedOverlay, only one bubble is opened at a time. 
	 * If you want more bubbles opened simultaneously, use many ItemizedOverlays. 
	 * 
	 * @param index of the overlay item to show
	 * @param mapView
	 */
	public void showBubbleOnItem(final int index, final MapView mapView) {
		ExtendedOverlayItem eItem = (ExtendedOverlayItem)(getItem(index)); 
		mItemWithBubble = eItem;
		if (eItem != null){
			showBubble(eItem, mBubble, mapView);
			//setFocus((Item)eItem);
		}
	}
	
	/**
	 * Populates this bubble with all item info:
	 * <ul>title and description in any case, </ul>
	 * <ul>image and sub-description if any.</ul> 
	 * and centers the map on the item. <br>
	 */
	public void showBubble(ExtendedOverlayItem item, InfoWindow bubble, MapView mapView){
		//offset the bubble to be top-centered on the marker:
		Drawable marker = getMarker(mapView.getZoomLevel())/*OverlayItem.ITEM_STATE_FOCUSED_MASK*/;
		int markerWidth = 0, markerHeight = 0;
		markerWidth = marker.getIntrinsicWidth(); 
		markerHeight = marker.getIntrinsicHeight();
		
		// TODO: check to remove height hacks.
		// TODO: check with other devices, smaller and bigger screens.
		if (marker.equals(mMarkerFocused)) {
			markerHeight += mDefaultMarker.getIntrinsicHeight() +1 ;
		} else {
			markerHeight += 12;
		}
		Point markerH = item.getHotspot(item.getMarkerHotspot(), markerWidth, markerHeight);
		Point bubbleH = item.getHotspot(HotspotPlace.TOP_CENTER, markerWidth, markerHeight);
		bubbleH.offset(-markerH.x, -markerH.y);
		
		bubble.open(item, bubbleH.x, bubbleH.y);

		mapView.getController().animateTo(item.getPoint());
	}
	
	/**
	 * Close the bubble (if it's opened). 
	 */
	public void hideBubble(){
		mBubble.close();
		mItemWithBubble = null;
	}
	
	@Override protected boolean onSingleTapUpHelper(final int index, final Item item, final MapView mapView) {
		showBubbleOnItem(index, mapView);
		return true;
	}
	
	/** @return the item currenty showing the bubble, or null if none.  */
	public OverlayItem getBubbledItem(){
		if (mBubble.isOpen())
			return mItemWithBubble;
		else
			return null;
	}
	
	/** @return the index of the item currenty showing the bubble, or -1 if none.  */
	public int getBubbledItemId(){
		OverlayItem item = getBubbledItem();
		if (item == null)
			return -1;
		else
			return mItemList.indexOf(item);
	}
	
	@Override public boolean removeItem(final Item item){
		boolean result = super.removeItem(item);
		if (mItemWithBubble == item){
			hideBubble();
		}
		return result;
	}
	
	@Override public void removeAllItems(){
		super.removeAllItems();
		hideBubble();
	}
	
	// ===========================================================
	// Methods from for draw and handle single tap ui according to zoom level.
	// ===========================================================

	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
		if (shadow) {
			return;
		}

		final int zoomLevel = mapView.getZoomLevel();
		final Projection projection = mapView.getProjection();
		final int size = this.mInternalItemList.size() - 1;

		/* Draw in backward cycle, so the items with the least index are on the front. */
		for (int i = size; i >= 0; i--) {
			final Item item = getItem(i);
			projection.toMapPixels(item.mGeoPoint, mCurScreenCoords);

			if (item != mItemWithBubble){
				onDrawItem(canvas, zoomLevel, item, mCurScreenCoords);
			}
		}
		
		onDrawFocusBubble(canvas, zoomLevel, projection);
	}
	
	protected void onDrawItem(final Canvas canvas, final int zoomLevel, final Item item, final Point curScreenCoords) {
		final Station station = (Station) (((ExtendedOverlayItem) item).getRelatedObject());
		final boolean zoomLevelDetailled = OverlayZoomUtils.isDetailledZoomLevel(zoomLevel);
		final Drawable marker = getDefaultMarker(zoomLevelDetailled, station.isStarred());
		
		boundToHotspot(marker, item.getMarkerHotspot());

		Overlay.drawAt(canvas, marker, curScreenCoords.x, curScreenCoords.y, false);
		if (zoomLevelDetailled) {
			mTitlePaint.setColor(mResourceProxy.getColor(ColorSelector.getColor(station.getBikes())));
			canvas.drawText(station.getStringBikes(), mCurScreenCoords.x	-16 * mScale, mCurScreenCoords.y - 45 * mScale, mTitlePaint); 
			mTitlePaint.setColor(mResourceProxy.getColor(ColorSelector.getColor(station.getAttachs())));
			canvas.drawText(station.getStringAttachs(), mCurScreenCoords.x	-16 * mScale, mCurScreenCoords.y - 22 * mScale, mTitlePaint); 
		}
	}
	
	private void onDrawFocusBubble(Canvas canvas, int zoomLevel, Projection projection) {
		if (mItemWithBubble != null){
			projection.toMapPixels(mItemWithBubble.mGeoPoint, mCurScreenCoords);
			onDrawItem(canvas, zoomLevel, (Item)mItemWithBubble, mCurScreenCoords);
		}
	}
	
	private Drawable getDefaultMarker(final boolean detailledZoomLevel, final boolean starred) {
		Drawable marker = mDefaultMarker;
		if (detailledZoomLevel) {
			marker = mMarkerFocused;
		} else if (starred) {
			marker = mMarkerStarred;
		}
		
		OverlayItem.setState(marker, 0);
		
		return marker;
	}
	
	@Override
	protected boolean hitTest(final int zoomLevel, Item item, android.graphics.drawable.Drawable marker, final int hitX,
			final int hitY) {
		if (OverlayZoomUtils.isDetailledZoomLevel(zoomLevel) && mMarkerFocused != null) {
			marker = mMarkerFocused;
		}
		return marker.getBounds().contains(hitX, hitY);
	}
	
	
	public Drawable getMarker(int zoomLevel) {
		if (OverlayZoomUtils.isDetailledZoomLevel(zoomLevel)) {
			return mMarkerFocused;
		}
		
		return mDefaultMarker;
	}
	
	public List<Item> getItems() {
		return super.mItemList;
	}
	
	public static final class OverlayZoomUtils {
		
		public static final int MIN_ZOOM_LEVEL_TO_DETAILS = 15;
		
		private OverlayZoomUtils() {}
		
		public static boolean isDetailledZoomLevel(int zoomLevel) {
			return zoomLevel > MIN_ZOOM_LEVEL_TO_DETAILS;
		}
	}
	
}
