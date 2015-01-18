package com.mxmariner.tides;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class StationOverlay extends Overlay {
    private ArrayList<Point> mPoints = new ArrayList<Point>();
    private int mPointsPrecomputed = 0;
    private float xoff = 0;
    private float yoff = 0;
//    protected Paint mPaintTide = new Paint();
//    protected Paint mPaintCurr = new Paint();
    private Bitmap tideBitmap;
    private Bitmap currBitmap;
    private final Point mTempPoint1 = new Point();
    private final Point mTempPoint2 = new Point();
    private int currentIndex = -1;
	
	public StationOverlay(Context ctx) {
		this(ctx, new DefaultResourceProxyImpl(ctx));
	}

	public StationOverlay(Context ctx, ResourceProxy pResourceProxy) {
		super(pResourceProxy);
		
//		mPaintTide.setColor(Color.MAGENTA);
//		mPaintCurr.setColor(Color.RED);
		
		tideBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.tidept);
		currBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.currentpt);
		xoff = Float.valueOf(tideBitmap.getWidth() / 2);
		yoff = Float.valueOf(tideBitmap.getHeight());
		
	}
	
	public void setStationsFromBox (final BoundingBoxE6 box, final MainActivity ma) {
		ma.getTideStationSubList().clear();
		ma.getCurrStationSubList().clear();
		
		final Projection pj = ma.getMapView().getProjection();
		
		final Rect clipBounds = pj.fromPixelsToProjected(pj.toPixels(box)); //mapPixels
		Point projectedPoint;
		
		for (int i = 0; i < currentIndex; i++) {
			projectedPoint = mPoints.get(i);
			if (clipBounds.contains(projectedPoint.x, projectedPoint.y)) {
				ma.getTideStationSubList().add(ma.getTideStationList().get(i));
				//stations.add(ma.getTideStationList().get(i));
			}
				
		}
		
		for (int i = currentIndex; i < mPoints.size(); i++) {
			projectedPoint = mPoints.get(i);
			if (clipBounds.contains(projectedPoint.x, projectedPoint.y))
				ma.getCurrStationSubList().add(ma.getCurrentStationList().get(i-currentIndex));
				//stations.add(ma.getCurrentStationList().get(i-currentIndex));
		}
	}
	
//	/**
//	 * 
//	 * @param box
//	 * @param mv
//	 * @return GeoPoint of first station found within box
//	 */
//	public GeoPoint getStationGeoPoint(final BoundingBoxE6 box, final MapView mv) {		
//		final Projection pj = mv.getProjection();
//		final Rect clipBounds = pj.fromPixelsToProjected(pj.toPixels(box)); //mapPixels
//		Point projectedPoint;
//		for (int i = 0; i < mPoints.size(); i++) {
//			projectedPoint = mPoints.get(i);
//			if (clipBounds.contains(projectedPoint.x, projectedPoint.y)) {
//				Point screenPoint = pj.toMapPixelsTranslated(projectedPoint, null);
//				pj.fromMapPixels(x, y, reuse)
//				return (GeoPoint) pj.fromPixels(screenPoint.x, screenPoint.y);
//			}
//				
//		}
//		return null;
//	}
	
	public void addStation(final Station station) {
		mPoints.add(new Point(station.getLatE6(), station.getLngE6()));
	}
	
	/**
	 * Only do this once and before adding currents or it does nothing
	 * @param stations
	 */
	public void addAllTideStations(final List<Station> stations) {
		if (currentIndex != -1) {
			return;
		}
		for (Station s: stations)
			addStation(s);
		
		currentIndex = stations.size();
	}
	
	/**
	 * Only do this once and always after add tide stations or this does nothing
	 * @param stations
	 */
	public void addAllCurrentStations(final List<Station> stations) {
		if (mPoints.size() != currentIndex) {
			return;
		}
		for (Station s: stations)
			addStation(s);
	}
	
	private Bitmap getBitmap(final int index) {
		if (index >= currentIndex) {
			return currBitmap;
		}
		return tideBitmap;
	}

	@Override
	protected void draw(final Canvas c, final MapView mapView, final boolean shadow) {
		if (shadow) {
            return;
		}
		
		final Projection pj = mapView.getProjection();
		
		// precompute new points to the intermediate projection.
        final int size = this.mPoints.size();

        while (this.mPointsPrecomputed < size) {
                final Point pt = this.mPoints.get(this.mPointsPrecomputed);
                pj.toMapPixelsProjected(pt.x, pt.y, pt);
                this.mPointsPrecomputed++;
        }
        
        Point screenPoint0 = null; // points on screen
        Point screenPoint1 = null;
        Point projectedPoint0; // points from the points list
        Point projectedPoint1;

        // clipping rectangle in the intermediate projection, to avoid performing projection.
        final Rect clipBounds = pj.fromPixelsToProjected(pj.getScreenRect());

        projectedPoint0 = this.mPoints.get(size - 1);

        for (int i = 0; i <= size - 1; i++) {
                // compute next points
                projectedPoint1 = this.mPoints.get(i);
                
                if (!clipBounds.contains(projectedPoint1.x, projectedPoint1.y)) {
                        // skip this line, move to next point
                        projectedPoint0 = projectedPoint1;
                        screenPoint0 = null;
                        continue;
                }

                // the starting point may be not calculated, because previous segment was out of clip
                // bounds
                if (screenPoint0 == null) {
                        screenPoint0 = pj.toMapPixelsTranslated(projectedPoint0, this.mTempPoint1);
                        c.drawBitmap(getBitmap(i), screenPoint0.x-xoff, screenPoint0.y-yoff, null);
                        //c.drawCircle(screenPoint0.x, screenPoint0.y, 2, mPaintTide);
                }

                screenPoint1 = pj.toMapPixelsTranslated(projectedPoint1, this.mTempPoint2);

                // skip this point, too close to previous point
                if (Math.abs(screenPoint1.x - screenPoint0.x) + Math.abs(screenPoint1.y - screenPoint0.y) <= xoff) {
                        continue;
                }
                c.drawBitmap(getBitmap(i), screenPoint1.x-xoff, screenPoint1.y-yoff, null);
                //c.drawCircle(screenPoint1.x, screenPoint1.y, 2, mPaintTide);

                // update starting point to next position
                projectedPoint0 = projectedPoint1;
                screenPoint0.x = screenPoint1.x;
                screenPoint0.y = screenPoint1.y;
        }

	}

}
