package com.mxmariner.fragment;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mxmariner.andxtidelib.IHarmonicsDatabaseService;
import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.tides.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

public class MapCameraListenerStationMarkerLoader implements GoogleMap.OnCameraChangeListener {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String TAG = MapCameraListenerStationMarkerLoader.class.getSimpleName();

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private GoogleMap googleMap;
    private Set<LatLng> latLngSet = new HashSet<>();
    private int lastZoom = 0;
    private Handler handler = new Handler();
    private IHarmonicsDatabaseService harmonicsDatabaseService;
    private StationType stationType = StationType.STATION_TYPE_TIDE;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private int getIconResourceId() {
        if (stationType == StationType.STATION_TYPE_TIDE) {
            return R.drawable.tidept;
        } else {
            return R.drawable.currentpt;
        }
    }

    public void loadStationMarkersAsync() {

        final LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    List<RemoteStation> remoteStationList = harmonicsDatabaseService.getStationsInBounds(stationType,
                            bounds.northeast.latitude, bounds.northeast.longitude,
                            bounds.southwest.latitude, bounds.southwest.longitude);
                    for (final RemoteStation station : remoteStationList) {
                        if (lastZoom < 9) {
                            final GeoHash geoHash = GeoHash.withBitPrecision(station.getLatitude(), station.getLongitude(), lastZoom * 2 + 5);
                            final WGS84Point center = geoHash.getBoundingBoxCenterPoint();
                            final LatLng latLng = new LatLng(center.getLatitude(), center.getLongitude());
                            if (!latLngSet.contains(latLng)) {
                                latLngSet.add(latLng);
                                final BoundingBox boundingBox = geoHash.getBoundingBox();
                                final PolygonOptions plyOptions = polygonFromGeoHashBox(boundingBox);
                                final int num = harmonicsDatabaseService.getStationsCountInBounds(stationType, boundingBox.getMaxLat(),
                                        boundingBox.getMaxLon(), boundingBox.getMinLat(), boundingBox.getMinLon());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        googleMap.addMarker(new MarkerOptions()
                                                .title(String.valueOf(num))
                                                .snippet(stationType.getTypeStr() + " station(s) here")
                                                .icon(BitmapDescriptorFactory.fromResource(getIconResourceId()))
                                                .position(latLng));
                                        googleMap.addPolygon(plyOptions);
                                    }
                                });
                            }
                        } else {
                            final LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
                            if (!latLngSet.contains(latLng)) {
                                latLngSet.add(latLng);
                                final String title = String.valueOf(station.getStationId());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        googleMap.addMarker(new MarkerOptions()
                                                .title(title)
                                                .icon(BitmapDescriptorFactory.fromResource(getIconResourceId()))
                                                .position(latLng));
                                    }
                                });
                            }
                        }
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "", e);
                }
                return null;
            }
        };
        task.execute();

    }

    private PolygonOptions polygonFromGeoHashBox(BoundingBox box) {
        WGS84Point ul = box.getUpperLeft();
        WGS84Point lr = box.getLowerRight();
        return new PolygonOptions()
                .add(new LatLng(ul.getLatitude(), ul.getLongitude()),
                        new LatLng(ul.getLatitude(), lr.getLongitude()),
                        new LatLng(lr.getLatitude(), lr.getLongitude()),
                        new LatLng(lr.getLatitude(), ul.getLongitude()));
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setHarmonicsDatabaseService(IHarmonicsDatabaseService harmonicsDatabaseService) {
        this.harmonicsDatabaseService = harmonicsDatabaseService;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region INNER CLASSES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region EVENTS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    /*~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^
                                               ANDROID
    ~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^~^*/


    //region LIFE CYCLE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region IMPLEMENTATION  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (googleMap != null && harmonicsDatabaseService != null) {

            int z = (int) cameraPosition.zoom;
            if (z != lastZoom) {
                googleMap.clear();
                latLngSet.clear();
                lastZoom = z;
            }
            loadStationMarkersAsync();
            Log.e(TAG, "onCameraChange: " + cameraPosition.toString());
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
