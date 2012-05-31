package com.vlille.checker.maps.overlay;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.vlille.checker.R;
import com.vlille.checker.maps.overlay.StationsOverlays.MyOverlayItem;
import com.vlille.checker.model.Station;

/**
 * A view representing a MapView marker information balloon.
 * <p>
 * This class has a number of Android resource dependencies:
 * <ul>
 * <li>drawable/balloon_overlay_bg_selector.xml</li>
 * <li>drawable/balloon_overlay_close.png</li>
 * <li>drawable/balloon_overlay_focused.9.png</li>
 * <li>drawable/balloon_overlay_unfocused.9.png</li>
 * <li>layout/balloon_map_overlay.xml</li>
 * </ul>
 * </p>
 * 
 * @author Jeff Gilfelt
 * 
 */
public class BalloonOverlayView<Item extends OverlayItem> extends FrameLayout {
	
	private LinearLayout mLinearLayout;
	private TextView mTextViewTitle;
	private CheckBox mFavoriteCheckBox;
	private int mBottomOffset;
	private Station station;

	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @param context
	 *            - The activity context.
	 * @param balloonBottomOffset
	 *            - The bottom padding (in pixels) to be applied when rendering this view.
	 */
	public BalloonOverlayView(final Context context, int offset) {
		super(context);

		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setVisibility(VISIBLE);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.maps_ballon_overlay, mLinearLayout);
		mTextViewTitle = (TextView) v.findViewById(R.id.balloon_name);
		mFavoriteCheckBox = (CheckBox) v.findViewById(R.id.balloon_item_favorite);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;
		addView(mLinearLayout, params);

	}
	
	public CheckBox getFavoriteCheckBox() {
		return mFavoriteCheckBox;
	}

	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item
	 *            - The overlay item containing the relevant view data (title and snippet).
	 */
	public void setData(MyOverlayItem item, Location location) {
		station = item.getStation();
		
		mTextViewTitle.setText(station.getName());
		mFavoriteCheckBox.setChecked(station.isStarred());
	}

	public void setBalloonBottomOffset(int offset) {
		int old = mBottomOffset;
		mBottomOffset = offset;
		if (old != mBottomOffset) {
			setPadding(10, 0, 10, offset);
			invalidate();
		}
	}

}
