package com.mxmariner.tides;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.mxmariner.util.Constants;

public class MyMapView extends MapView {
	public static final String TAG = "MyMapView";
	public static final int MAXZOOM = 12;
	public static final int MINZOOM = 4;
	public static final int TILESIZE = 256;
	
	private StationOverlay stationOverlay;
	private PathOverlay tapBox;
	private BoundingBoxE6 tapBoundBox;
	private float tapPx;
	private Handler mHandler = new Handler();
	private Runnable tapRunnable;
	

	public MyMapView(Context context, AttributeSet attrs) {
		this(context);
	}
	 
	public MyMapView (final Context context) {
		this(context, new DefaultResourceProxyImpl(context), getProviderArray(context), null);
	}
	
	private MyMapView(final Context context, final ResourceProxy resourceProxy, final MapTileProviderBase aTileProvider, final Handler tileRequestCompleteHandler) {
		super(context, TILESIZE, resourceProxy, aTileProvider, tileRequestCompleteHandler, null);
		getController().setZoom(MINZOOM);
		
		setup(context);
	}
	
	private void setup(final Context context) {
		final MainActivity mainActivity = (MainActivity) context;
		mainActivity.setMapView(this);
		
		stationOverlay = new StationOverlay(mainActivity, getResourceProxy());
		
		stationOverlay.addAllTideStations(mainActivity.getTideStationList());
		stationOverlay.addAllCurrentStations(mainActivity.getCurrentStationList());
        stationOverlay.setEnabled(true);
        getOverlayManager().add(stationOverlay);
        
        setMultiTouchControls(true);
        
        final int zoom = mainActivity.getPrefs().getInt("zoom", 4);
        final int latE6 = mainActivity.getPrefs().getInt("latE6", 39828200);
    	final int lngE6 = mainActivity.getPrefs().getInt("lngE6", -98579500);
    	final GeoPoint center = new GeoPoint(latE6, lngE6);
    	getController().setZoom(zoom);
    	getController().setCenter(center);
        
        if (mainActivity.getLocation() != null) {
        	final SimpleLocationOverlay locationOverlay = new SimpleLocationOverlay(mainActivity, getResourceProxy());
            locationOverlay.setEnabled(true);
            locationOverlay.setLocation(new GeoPoint(mainActivity.getLocation().getLatitude(), mainActivity.getLocation().getLongitude()));
            getOverlayManager().add(locationOverlay);
        }
		
        
        //size of tap box of 5mm in pixels
        tapPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 5, 
                getResources().getDisplayMetrics());
        
        tapBox = new PathOverlay(Color.RED, context);
        tapBox.getPaint().setStrokeWidth(4.0f);
        getOverlayManager().add(tapBox);
        
        setMapListener(new DelayedMapListener(new MapListener() {  
            public boolean onZoom(final ZoomEvent e) {
            	mainActivity.setUseMapBox(true);
            	mainActivity.setMapBox(getBoundingBox());
                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
            	mainActivity.setUseMapBox(true);
            	mainActivity.setMapBox(getBoundingBox());
                return true;
            }
        }, 100 ));
        
        setupGestureListener();
        
        tapRunnable = new Runnable() {
        	
			@Override
			public void run() {
				final MainActivity ma = ((MainActivity) getContext());
				
				//set sub-station lists
				stationOverlay.setStationsFromBox(tapBoundBox, mainActivity);
				
				final ArrayList<String> selections = new ArrayList<String>();
				
				final int t = ma.getTideStationSubList().size();
				final int c = ma.getCurrStationSubList().size();
				
				if (t>0)
					selections.add(String.format("%s - Tide Stations", t));
				if (c>0)
					selections.add(String.format("%s - Current Stations", c));
				
				final int total = t+c;
				
				if (total >= 1) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
			        builder.setTitle("Stations");
			        builder.setItems(selections.toArray(new String[selections.size()]), new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int position) {
			            	if (t==0)
			            		position ++;
			            	
			            	ma.setUseMapBox(false);
			            	
			            	switch (position) {
			            	case 0:
			            		if (t==1) {
			            			ma.setSelectedStation(ma.getTideStationSubList().get(0));
			            			ma.goToStationTab();
			            		} else {
			            			ma.goToTideTab();
			            		}
			            		break;
			            	case 1:
			            		if (c==1) {
			            			ma.setSelectedStation(ma.getCurrStationSubList().get(0));
			            			ma.goToStationTab();
			            		} else {
			            			ma.goToCurrentTab();
			            		}
			            		break;
			            	}
			            }
			        });
			        final Dialog dialog = builder.create();
			        dialog.show();
				}
			}
        	
        };
	}
	
	private void setupGestureListener() {
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
    		@Override
    		public boolean onDown(final MotionEvent e) {
    			tapBox.setEnabled(false);
    			return false;
    		}
    		
    		@Override
    	 	public void onLongPress(MotionEvent e) {
    			makeTapBox(e);
    		}
        	
    		@Override
    		public boolean onSingleTapConfirmed (MotionEvent e) {
    			makeTapBox(e);
    			return true;
    		}
        };
        
        final GestureDetector gestureDetector = new GestureDetector(getContext(), gestureListener);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }
	
	private void makeTapBox(final MotionEvent ev) {
		tapBox.clearPath();
		tapBox.setEnabled(true);
		float centerX = ev.getX();
		float centerY = ev.getY();
		Projection pj = getProjection();
		GeoPoint northEast = (GeoPoint) pj.fromPixels(centerX+tapPx, centerY+tapPx);
		GeoPoint southWest = (GeoPoint) pj.fromPixels(centerX-tapPx, centerY-tapPx);
		GeoPoint southEast = new GeoPoint(southWest.getLatitudeE6(), northEast.getLongitudeE6());
		GeoPoint northWest = new GeoPoint(northEast.getLatitudeE6(), southWest.getLongitudeE6());
		
		
		tapBoundBox = new BoundingBoxE6(northEast.getLatitudeE6(), northEast.getLongitudeE6(), 
				southWest.getLatitudeE6(), southWest.getLongitudeE6());
		
		tapBox.addPoint(northEast);
		tapBox.addPoint(southEast);
		tapBox.addPoint(southWest);
		tapBox.addPoint(northWest);
		tapBox.addPoint(northEast);
		
		mHandler.removeCallbacks(tapRunnable);
		mHandler.postDelayed(tapRunnable, 100);
		
		invalidate();
	}
	
	@Override
	 public void onSizeChanged(int w, int h, int oldw, int oldh) {
	  //getWith() and getHeight() are 0 until this is called
	  final MainActivity mainActivity = (MainActivity) getContext();
	  mainActivity.setMapBox(getBoundingBox(w, h));
	 }
	
	@Override
	public int getMaxZoomLevel() {
		return MAXZOOM;
	}

	@Override
	public int getMinZoomLevel() {
		return MINZOOM;
	}
	
	private static MapTileProviderArray getProviderArray(final Context context) {
		
		final MapTileModuleProviderBase[] myProviders = new MapTileModuleProviderBase[1];
		final MapsforgeOSMTileSource mots = new MapsforgeOSMTileSource("world");
		mots.setMapFile(Constants.GetMXWorldFile());
		final MapsforgeOSMDroidTileProvider motp = new MapsforgeOSMDroidTileProvider(null, new NetworkAvailabliltyCheck(context));
		motp.setTileSource(mots);
		myProviders[0] = motp;
		final SimpleRegisterReceiver srr = new SimpleRegisterReceiver(context);
		final MapTileProviderArray myProvider = new MapTileProviderArray(mots, srr, myProviders);
		
		return myProvider;
	}

}
