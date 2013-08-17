package com.vlille.checker.ui.osm.overlay;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.vlille.checker.R;
import com.vlille.checker.ui.osm.PositionTransformer;
import com.vlille.checker.utils.ContextHelper;

public class CircleLocationOverlay extends SimpleLocationOverlay {

    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GeoPoint geoPosition;

    public CircleLocationOverlay(Context context) {
        super(context);

        paint.setColor(context.getResources().getColor(R.color.mapview_location_circle));
        paint.setAlpha(125);
    }

    public void setGeoPosition(GeoPoint geoPoint) {
        this.geoPosition = geoPoint;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow || geoPosition == null) {
            return;
        }

        final long radiusInPrefs = ContextHelper.getRadiusValue(mapView.getContext());
        final float radiusInMeters = metersToRadius(radiusInPrefs, mapView, mapView.getMapCenter());

        Point mapCenterPoint = new Point();
        mapView.getProjection().toPixels(geoPosition, mapCenterPoint);

        canvas.drawCircle(mapCenterPoint.x, mapCenterPoint.y, radiusInMeters, this.paint);
    }

    private float metersToRadius(long meters, MapView mapView, IGeoPoint mapCenter) {
        return (float) (
                mapView.getProjection().metersToEquatorPixels(meters)
                        * (1 / Math.cos(Math.toRadians(PositionTransformer.toNormal(mapCenter.getLatitudeE6()))))
        );
    }

}
