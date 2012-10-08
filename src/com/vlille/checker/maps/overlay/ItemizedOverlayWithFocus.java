package com.vlille.checker.maps.overlay;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.vlille.checker.utils.ColorSelector;

public class ItemizedOverlayWithFocus<Item extends OverlayItem> extends ItemizedIconOverlay<Item> {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MIN_ZOOM_LEVEL_TO_DETAILS = 15;
	public static final int DESCRIPTION_BOX_PADDING = 3;
	public static final int DESCRIPTION_BOX_CORNERWIDTH = 3;

	public static final int DESCRIPTION_LINE_HEIGHT = 12;
	/** Additional to <code>DESCRIPTION_LINE_HEIGHT</code>. */
	public static final int DESCRIPTION_TITLE_EXTRA_LINE_HEIGHT = 2;

	// protected static final Point DEFAULTMARKER_FOCUSED_HOTSPOT = new Point(10, 19);
	protected static final int DEFAULTMARKER_BACKGROUNDCOLOR = Color.rgb(101, 185, 74);

	protected static final int DESCRIPTION_MAXWIDTH = 200;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mMarkerFocusedBackgroundColor;
	protected final Paint mMarkerBackgroundPaint, mDescriptionPaint, mTitlePaint;
	protected final int mTextSize;	

	protected Drawable mMarkerFocusedBase;
	protected int mFocusedItemIndex;
	protected boolean mFocusItemsOnTap;

	// ===========================================================
	// Constructors
	// ==========================================================

	public ItemizedOverlayWithFocus(final Context ctx, final List<Item> aList,
			final OnItemGestureListener<Item> aOnItemTapListener) {
		this(aList, aOnItemTapListener, new DefaultResourceProxyImpl(ctx));
	}

	public ItemizedOverlayWithFocus(final List<Item> aList,
			final OnItemGestureListener<Item> aOnItemTapListener, final ResourceProxy pResourceProxy) {
		this(aList, pResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default), null, 12, NOT_SET,
				aOnItemTapListener, pResourceProxy);
	}

	public ItemizedOverlayWithFocus(final List<Item> aList,
			final Drawable pMarker,
			final Drawable pMarkerFocused,
			final int pTextSize,
			int pFocusedBackgroundColor,
			final OnItemGestureListener<Item> aOnItemTapListener, final ResourceProxy pResourceProxy) {

		super(aList, pMarker, aOnItemTapListener, pResourceProxy);

		this.mTextSize = pTextSize;
		if (pMarkerFocused == null) {
			this.mMarkerFocusedBase = boundToHotspot(
					mResourceProxy.getDrawable(ResourceProxy.bitmap.marker_default_focused_base),
					HotspotPlace.BOTTOM_CENTER);
		} else
			this.mMarkerFocusedBase = pMarkerFocused;

		this.mMarkerFocusedBackgroundColor = (pFocusedBackgroundColor != NOT_SET) ? pFocusedBackgroundColor
				: DEFAULTMARKER_BACKGROUNDCOLOR;

		this.mMarkerBackgroundPaint = new Paint(); // Color is set in onDraw(...)

		this.mDescriptionPaint = new Paint();
		this.mDescriptionPaint.setAntiAlias(true);
		
		this.mTitlePaint = new Paint();
		initPaint(pResourceProxy);
		this.unSetFocusedItem();
	}

	private void initPaint(final ResourceProxy pResourceProxy) {
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
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected boolean onSingleTapUpHelper(final int index, final Item item, final MapView mapView) {
		if (this.mFocusItemsOnTap) {
			this.mFocusedItemIndex = index;
			mapView.postInvalidate();
		}
		return this.mOnItemGestureListener.onItemSingleTapUp(index, item);
	}

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

			onDrawItem(canvas, zoomLevel, item, mCurScreenCoords);
		}
	}
	
	protected void onDrawItem(final Canvas canvas, final int zoomLevel, final Item item, final Point curScreenCoords) {
		Drawable marker = getDefaultMarker(0);
		final boolean zoomLevelDetailled = zoomLevel > MIN_ZOOM_LEVEL_TO_DETAILS;
		if (zoomLevelDetailled) {
			marker = mMarkerFocusedBase;
		}
		boundToHotspot(marker, item.getMarkerHotspot());

		Overlay.drawAt(canvas, marker, curScreenCoords.x, curScreenCoords.y, false);
		if (zoomLevelDetailled) {
			mTitlePaint.setColor(mResourceProxy.getColor(ColorSelector.getColor(4)));
			canvas.drawText("35", mCurScreenCoords.x	-16 * mScale, mCurScreenCoords.y - 22 * mScale, mTitlePaint); 
			mTitlePaint.setColor(mResourceProxy.getColor(ColorSelector.getColor(0)));
			canvas.drawText("15", mCurScreenCoords.x	-16 * mScale, mCurScreenCoords.y - 45 * mScale, mTitlePaint); 
		}
	}
	
	private Drawable getDefaultMarker(final int state) {
		OverlayItem.setState(mDefaultMarker, state);
		return mDefaultMarker;
	}

}
