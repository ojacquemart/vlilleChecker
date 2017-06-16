package com.vlille.checker.ui.osm.overlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.ui.osm.overlay.window.InfoWindow;
import com.vlille.checker.utils.color.ColorSelector;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import java.util.List;

public class ItemizedOverlayWithFocus<T extends MaskableOverlayItem> extends ItemizedIconOverlay<T> {

    private static final String TAG = "ItemOverlayWithFocus";

    // ===========================================================
    // Fields
    // ===========================================================

    private Context mContext;
    private float mScale;
    private final Point mCurScreenCoords = new Point();
    private Paint mTitlePaint;

    protected int mTextSize;

    protected Drawable mMarkerDetails;
    protected Drawable mMarkerPin;
    protected Drawable mMarkerPinStarred;
    protected int mFocusedItemIndex;
    protected boolean mFocusItemsOnTap;

    // ===========================================================
    // osmdroid bonus bubble
    // ===========================================================

    protected InfoWindow mBubble = null;
    protected OverlayItem mItemWithBubble = null;

    // ===========================================================
    // Constructors
    // ==========================================================

    public ItemizedOverlayWithFocus(
            final List<T> aList,
            final Resources resources,
            final InfoWindow pInfoWindow,
            final OnItemGestureListener<T> aOnItemTapListener,
            final Context context) {
        super(aList, ContextCompat.getDrawable(context, R.drawable.ic_station_marker), aOnItemTapListener, context);

        this.mContext = context;

        this.mScale = resources.getDisplayMetrics().density;
        this.mMarkerDetails = ContextCompat.getDrawable(context,R.drawable.ic_station_marker);
        this.mMarkerPin = ContextCompat.getDrawable(context,R.drawable.ic_station_pin);
        this.mMarkerPinStarred = ContextCompat.getDrawable(context,R.drawable.ic_station_pin_star);
        this.mBubble = pInfoWindow;
        this.mTextSize = resources.getDimensionPixelSize(R.dimen.overlay_font_size);

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

    public void unSetFocusedItem() {
        this.mFocusedItemIndex = NOT_SET;
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
     * @param index   of the overlay item to show
     * @param mapView
     */
    public void showBubbleOnItem(final int index, final MapView mapView) {
        Log.d(TAG, "showBubbleOnItem");
        ExtendedOverlayItem eItem = getItem(index);
        mItemWithBubble = eItem;
        if (eItem != null) {
            showBubble(eItem, mBubble, mapView);
        }
    }

    /**
     * Populates this bubble with all item info:
     * <ul>title and description in any case, </ul>
     * <ul>image and sub-description if any.</ul>
     * and centers the map on the item. <br>
     */
    public void showBubble(ExtendedOverlayItem item, InfoWindow bubble, MapView mapView) {
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
    public void hideBubble() {
        mBubble.close();
        mItemWithBubble = null;
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final T item, final MapView mapView) {
        showBubbleOnItem(index, mapView);
        return true;
    }

    public InfoWindow getBubble() {
        return mBubble;
    }

    @Override
    public boolean removeItem(final T item) {
        boolean result = super.removeItem(item);

        if (mItemWithBubble.equals(item)) {
            hideBubble();
        }
        return result;
    }

    @Override
    public void removeAllItems() {
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

		// Draw in backward cycle, so the items with the least index are on the front.
        for (int i = size; i >= 0; i--) {
            final T item = getItem(i);
            projection.toPixels(item.getPoint(), mCurScreenCoords);

            if (item != mItemWithBubble) {
                onDrawItem(canvas, zoomLevel, item, mCurScreenCoords);
            }
        }

        onDrawFocusBubble(canvas, zoomLevel, projection);
    }

    private void onDrawItem(final Canvas canvas, final int zoomLevel, final T item, final Point curScreenCoords) {
        MaskableOverlayItem maskableItem = item;
        if (maskableItem.isHidden()) {
            return;
        }

        final Station station = (Station) item.getRelatedObject();
        final boolean zoomLevelDetailled = OverlayZoomUtils.isDetailledZoomLevel(zoomLevel);
        final Drawable marker = getDefaultMarker(zoomLevelDetailled, station.isStarred());

        boundToHotspot(marker, HotspotPlace.BOTTOM_CENTER);

        Overlay.drawAt(canvas, marker, curScreenCoords.x, curScreenCoords.y, false, 0);
        if (zoomLevelDetailled) {
            mTitlePaint.setColor(ColorSelector.getColorForMap(mContext, station.getBikes()));
            canvas.drawText(station.getBikesAsString(), mCurScreenCoords.x - (8 * mScale), mCurScreenCoords.y - (26 * mScale), mTitlePaint);
            mTitlePaint.setColor(ColorSelector.getColorForMap(mContext, station.getAttachs()));
            canvas.drawText(station.getAttachsAsString(), mCurScreenCoords.x - (8 * mScale), mCurScreenCoords.y - (13 * mScale), mTitlePaint);
        }
    }

    private void onDrawFocusBubble(Canvas canvas, int zoomLevel, Projection projection) {
        if (mItemWithBubble != null) {
            projection.toPixels(mItemWithBubble.getPoint(), mCurScreenCoords);
            onDrawItem(canvas, zoomLevel, (T) mItemWithBubble, mCurScreenCoords);
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

        return marker;
    }

    public Drawable getMarker(int zoomLevel) {
        Log.d(TAG, "getMarker");
        if (OverlayZoomUtils.isDetailledZoomLevel(zoomLevel)) {
            return mMarkerDetails;
        }

        return mMarkerPin;
    }

    public List<T> getItems() {
        return super.mItemList;
    }

}
