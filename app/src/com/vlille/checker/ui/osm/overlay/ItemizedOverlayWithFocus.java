package com.vlille.checker.ui.osm.overlay;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.osm.overlay.window.InfoWindow;
import com.vlille.checker.utils.color.ColorSelector;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import java.util.List;

public class ItemizedOverlayWithFocus<Item extends MaskableOverlayItem> extends ItemizedIconOverlay<Item> {

    private static final String TAG = "ItemOverlayWithFocus";

    // ===========================================================
    // osmdroid bonus bubble
    // ===========================================================

    protected InfoWindow mBubble = null;
    protected OverlayItem mItemWithBubble = null;

    // ===========================================================
    // Fields
    // ===========================================================

    private final Point mCurScreenCoords = new Point();
    private Paint mDescriptionPaint, mTitlePaint;
    protected int mTextSize;

    protected Drawable mMarkerDetails;
    protected Drawable mMarkerPin;
    protected Drawable mMarkerPinStarred;
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
        super(aList, resources.getDrawable(R.drawable.ic_station_marker), aOnItemTapListener, pResourceProxy);

        this.mMarkerDetails = resources.getDrawable(R.drawable.ic_station_marker);
        this.mMarkerPin = resources.getDrawable(R.drawable.ic_station_pin);
        this.mMarkerPinStarred = resources.getDrawable(R.drawable.ic_station_pin_star);
        this.mBubble = pInfoWindow;
        this.mTextSize = resources.getDimensionPixelSize(R.dimen.overlay_font_size);

        this.mDescriptionPaint = new Paint();
        this.mDescriptionPaint.setAntiAlias(true);
        this.initPaint();

        mItemWithBubble = null;
        this.unSetFocusedItem();
        this.hideBubble();
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
        Log.d(TAG, "showBubbleOnItem");
        ExtendedOverlayItem eItem = getItem(index);
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
        Log.d(TAG, "showBubble");
        //offset the bubble to be top-centered on the marker:
        Drawable marker = getMarker(mapView.getZoomLevel());
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();

        Point markerH = item.getHotspot(HotspotPlace.BOTTOM_CENTER, markerWidth, markerHeight);
        Point bubbleH = item.getHotspot(HotspotPlace.BOTTOM_CENTER, markerWidth, markerHeight);
        bubbleH.offset(-markerH.x, -markerH.y - markerHeight);
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
        final int size = this.mItemList.size() - 1;

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
        MaskableOverlayItem maskableItem = item;
        if (maskableItem.isHidden()) {
            return;
        }

        final Station station = (Station) item.getRelatedObject();
        final boolean zoomLevelDetailled = OverlayZoomUtils.isDetailledZoomLevel(zoomLevel);
        final Drawable marker = getDefaultMarker(zoomLevelDetailled, station.isStarred());

        boundToHotspot(marker, HotspotPlace.BOTTOM_CENTER);

        Overlay.drawAt(canvas, marker, curScreenCoords.x, curScreenCoords.y, false);
        if (zoomLevelDetailled) {
            mTitlePaint.setColor(getResourceProxy().getColor(ColorSelector.getColorForMap(station.getBikes())));
            canvas.drawText(station.getStringBikes(), mCurScreenCoords.x - (8 * mScale), mCurScreenCoords.y - (30 * mScale), mTitlePaint);
            mTitlePaint.setColor(getResourceProxy().getColor(ColorSelector.getColorForMap(station.getAttachs())));
            canvas.drawText(station.getStringAttachs(), mCurScreenCoords.x - (8 * mScale), mCurScreenCoords.y - (15 * mScale), mTitlePaint);
        }
    }

    private ResourceProxyImpl getResourceProxy() {
        return (ResourceProxyImpl) mResourceProxy;
    }

    private void onDrawFocusBubble(Canvas canvas, int zoomLevel, Projection projection) {
        if (mItemWithBubble != null){
            projection.toMapPixels(mItemWithBubble.mGeoPoint, mCurScreenCoords);
            onDrawItem(canvas, zoomLevel, (Item)mItemWithBubble, mCurScreenCoords);
        }
    }

    /**
     * Gets the marker to draw.
     */
    private Drawable getDefaultMarker(final boolean detailledZoomLevel, final boolean starred) {
        Drawable marker = mMarkerPin;
		if (detailledZoomLevel) {
			marker = mMarkerDetails;
		} else if (starred) {
			marker = mMarkerPinStarred;
		}

        //OverlayItem.setState(marker, 0);

        return marker;
    }

    protected boolean hitTest(final int zoomLevel, Item item, Drawable marker, final int hitX,
                              final int hitY) {
        Log.d(TAG, "hitTest");
        if (OverlayZoomUtils.isDetailledZoomLevel(zoomLevel)) {
            marker = mMarkerDetails;
        } else {
            marker = mMarkerPin;
        }
        return marker.getBounds().contains(hitX, hitY);
    }


    public Drawable getMarker(int zoomLevel) {
        Log.d(TAG, "getMarker");
        if (OverlayZoomUtils.isDetailledZoomLevel(zoomLevel)) {
            return mMarkerDetails;
        }

        return mMarkerPin;
    }

    public List<Item> getItems() {
        return super.mItemList;
    }

}
