package com.vlille.checker.maps.overlay;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.vlille.checker.maps.PositionTransformer;
import com.vlille.checker.utils.ContextHelper;

public class PositionCircleOverlay extends Overlay {
	
	private int latitudeE6;
	private int longitudeE6;
	
	private Paint paint;

	public PositionCircleOverlay(int latitudeE6, int longitudeE6) {
		this.latitudeE6 = latitudeE6;
		this.longitudeE6 = longitudeE6;
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xff3A8C19);
		paint.setAlpha(125);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow) {
			Point centerPoint = new Point();
			Projection projection = mapView.getProjection();
			final GeoPoint mapCenter = new GeoPoint(latitudeE6, longitudeE6);
			projection.toPixels(mapCenter, centerPoint);

			final float metersRadius = metersToRadius(ContextHelper.getRadiusValue(mapView.getContext()), mapView, mapCenter);
			canvas.drawCircle((float) centerPoint.x, (float) centerPoint.y, metersRadius, paint);

			super.draw(canvas, mapView, shadow);
		}
	}
	
	private float metersToRadius(long meters, MapView mapView, GeoPoint mapCenter)  {
		return (float) (
					mapView.getProjection().metersToEquatorPixels(meters) 
					* (1/ Math.cos(Math.toRadians(PositionTransformer.toNormal(mapCenter.getLatitudeE6()))))
				);
	}
}
