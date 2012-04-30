package com.vlille.checker.maps.overlay;

import android.content.Context;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.vlille.checker.R;
import com.vlille.checker.maps.overlay.StationsOverlays.MyOverlayItem;
import com.vlille.checker.model.Station;
import com.vlille.checker.utils.ContextHelper;

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
	private Context mContext;
	private String mStationId = null;

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

		this.mContext = context;
		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setVisibility(VISIBLE);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.maps_ballon_overlay, mLinearLayout);
		mTextViewTitle = (TextView) v.findViewById(R.id.balloon_name);
		mFavoriteCheckBox = (CheckBox) v.findViewById(R.id.balloon_item_favorite);
		mFavoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton checkBox, boolean checked) {
				if (mStationId != null) {
					ContextHelper.registerPrefsStation(context, mStationId.toString(), checked);
				}
			}
		});
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;
		addView(mLinearLayout, params);

	}

	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item
	 *            - The overlay item containing the relevant view data (title and snippet).
	 */
	public void setData(MyOverlayItem item, Location location) {
		final Station station = item.getStation();
		
		mStationId = station.getId();
		mTextViewTitle.setText(station.getName());
		mFavoriteCheckBox.setChecked(ContextHelper.isStarred(mContext, station.getId()));
		// mId = String
		// .valueOf(((StationsOverlay.StationOverlay) item)
		// .getId());
		// Cursor station = ((Activity) mContext).managedQuery(Uri
		// .withAppendedPath(StationsProvider.CONTENT_URI, mId), new String[] {
		// OpenBikeDBAdapter.KEY_NAME, OpenBikeDBAdapter.KEY_OPEN,
		// OpenBikeDBAdapter.KEY_FAVORITE, OpenBikeDBAdapter.KEY_BIKES,
		// OpenBikeDBAdapter.KEY_SLOTS }, null, null, null);
		// mLinearLayout.setVisibility(VISIBLE);
		// String name = station.getString(station
		// .getColumnIndex(OpenBikeDBAdapter.KEY_NAME));
		// mTextViewTitle.setText(name);
		//
		// if (station.getInt(station
		// .getColumnIndex(OpenBikeDBAdapter.KEY_FAVORITE)) == 1) {
		// mFavoriteCheckBox.setChecked(true);
		// } else {
		// mFavoriteCheckBox.setChecked(false);
		// }
		//
		// if (station.getInt(station.getColumnIndex(OpenBikeDBAdapter.KEY_OPEN)) == 1) {
		// // Opened station
		// int bikes = station.getInt(station
		// .getColumnIndex(OpenBikeDBAdapter.KEY_BIKES));
		// int slots = station.getInt(station
		// .getColumnIndex(OpenBikeDBAdapter.KEY_SLOTS));
		// mBikesTextView.setText(mContext.getResources().getQuantityString(
		// R.plurals.bike, bikes, bikes));
		// mSlotsTextView.setText(mContext.getResources().getQuantityString(
		// R.plurals.slot, slots, slots));
		// mClosedTextView.setVisibility(GONE);
		// mBikesTextView.setVisibility(VISIBLE);
		// mSlotsTextView.setVisibility(VISIBLE);
		// } else {
		// // Closed station
		// mClosedTextView.setVisibility(VISIBLE);
		// mBikesTextView.setVisibility(GONE);
		// mSlotsTextView.setVisibility(GONE);
		// }
		// GeoPoint point = item.getPoint();
		// int distance = Utils.computeDistance(point.getLatitudeE6(), point.getLongitudeE6(), location);
		//
		// if (distance == LocationService.DISTANCE_UNAVAILABLE) {
		// // No distance to show
		// mDistanceTextView.setVisibility(GONE);
		// } else {
		// // Show distance
		// mDistanceTextView.setVisibility(VISIBLE);
		// mDistanceTextView.setText(mContext.getString(R.string.at) + " " + Utils.formatDistance(distance));
		// }
	}

	public void setBalloonBottomOffset(int offset) {
		int old = mBottomOffset;
		mBottomOffset = offset;
		if (old != mBottomOffset) {
			setPadding(10, 0, 10, offset);
			invalidate();
		}
	}

	// private void showStationDetails(Uri uri) {
	// Intent intent = new Intent(mContext, StationDetails.class)
	// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// intent.setAction(Intent.ACTION_VIEW);
	// intent.setData(uri);
	// mContext.startActivity(intent);
	// }
	//
	// private void showStationDetails(String id) {
	// showStationDetails(Uri.withAppendedPath(StationsProvider.CONTENT_URI,
	// id));
	// }
}
