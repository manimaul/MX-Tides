package com.mxmariner.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mxmariner.andxtidelib.IHarmonicsDatabaseService;
import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.event.Signals;
import com.mxmariner.tides.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;

public class GoogleMapCommunicator implements GoogleMap.OnCameraChangeListener,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String TAG = GoogleMapCommunicator.class.getSimpleName();

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private GoogleMap googleMap;
    private Map<LatLng, BoundingBox> groupedStationMarkers = new HashMap<>();
    private Map<LatLng, Long> stationMarkers = new HashMap<>();
    private int lastZoom = 0;
    private Handler handler = new Handler();
    private IHarmonicsDatabaseService harmonicsDatabaseService;
    private final StationType stationType;
    private final Context context;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public GoogleMapCommunicator(Context context, StationType type) {
        this.context = context;
        this.stationType = type;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setHarmonicsDatabaseService(IHarmonicsDatabaseService harmonicsDatabaseService) {
        this.harmonicsDatabaseService = harmonicsDatabaseService;
    }

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
                            final GeoHash geoHash = GeoHash.withBitPrecision(station.getLatitude(),
                                    station.getLongitude(), lastZoom * 2 + 5);
                            final WGS84Point center = geoHash.getBoundingBoxCenterPoint();
                            final LatLng latLng = new LatLng(center.getLatitude(), center.getLongitude());
                            if (!groupedStationMarkers.containsKey(latLng)) {
                                groupedStationMarkers.put(latLng, geoHash.getBoundingBox());
                                final BoundingBox boundingBox = geoHash.getBoundingBox();
                                final PolygonOptions plyOptions = polygonFromGeoHashBox(boundingBox);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        googleMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(getIconResourceId()))
                                                .position(latLng));
                                        googleMap.addPolygon(plyOptions);
                                    }
                                });
                            }
                        } else {
                            final LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
                            if (!stationMarkers.containsKey(latLng)) {
                                stationMarkers.put(latLng, station.getStationId());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        googleMap.addMarker(new MarkerOptions()
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
    public void onInfoWindowClick(Marker marker) {
        if (googleMap != null && harmonicsDatabaseService != null) {
            if (stationMarkers.containsKey(marker.getPosition())) {
                final Long id = stationMarkers.get(marker.getPosition());
                Signals.getInstance()
                        .pusblishStationIdEvent(id);

            } else if (groupedStationMarkers.containsKey(marker.getPosition())) {
                BoundingBox boundingBox = groupedStationMarkers.get(marker.getPosition());
                LatLng southWest = new LatLng(boundingBox.getMinLat(), boundingBox.getMinLon());
                LatLng northEast = new LatLng(boundingBox.getMaxLat(), boundingBox.getMaxLon());
                LatLngBounds bounds = new LatLngBounds(southWest, northEast);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (googleMap != null && harmonicsDatabaseService != null) {
            if (stationMarkers.containsKey(marker.getPosition())) {
                long id = stationMarkers.get(marker.getPosition());
                long epoch = Calendar.getInstance().getTime().getTime() / 1000;
                try {
                    int options = RemoteStationData.REQUEST_OPTION_PREDICTION;
                    RemoteStationData remoteStationData = harmonicsDatabaseService.getDataForTime(id, epoch, options);
                    marker.setTitle(remoteStationData.getName());
                    String snippet = context.getString(R.string.prediction_now) +
                            " " +
                            remoteStationData.getOptionalPrediction();
                    marker.setSnippet(snippet);
                } catch (RemoteException e) {
                    Log.e(TAG, "", e);
                }
            } else if (groupedStationMarkers.containsKey(marker.getPosition())) {
                BoundingBox boundingBox = groupedStationMarkers.get(marker.getPosition());
                try {
                    int count = harmonicsDatabaseService.getStationsCountInBounds(stationType,
                            boundingBox.getMaxLat(), boundingBox.getMaxLon(),
                            boundingBox.getMinLat(), boundingBox.getMinLon());
                    marker.setTitle(count + " " + stationType.getTypeStr() + " station(s) here");
                } catch (RemoteException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (googleMap != null && harmonicsDatabaseService != null) {

            int z = (int) cameraPosition.zoom;
            if (z != lastZoom) {
                googleMap.clear();
                groupedStationMarkers.clear();
                stationMarkers.clear();
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
