package com.mxmariner.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.tides.R;
import com.mxmariner.util.GoogleMapCommunicator;
import com.mxmariner.util.HarmonicsServiceConnection;
import com.mxmariner.util.MXPreferences;

public class MXTideMapFragment extends MXMainFragment {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private GoogleMapCommunicator googleMapCommunicator;
    private HarmonicsServiceConnection serviceConnection = new HarmonicsServiceConnection();
    private MXPreferences mxPreferences;
    private Context context;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static MXTideMapFragment createFragment(MXMainFragmentId fragmentId, Bundle args) {
        MXTideMapFragment fragment = new MXTideMapFragment();
        args = args == null ? new Bundle() : args ;
        args.putSerializable(MXMainFragmentId.class.getSimpleName(), fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mxPreferences = new MXPreferences(context);
        StationType type = getFragmentId() == MXMainFragmentId.MAP_FRAGMENT_TIDES ?
                StationType.STATION_TYPE_TIDE : StationType.STATION_TYPE_CURRENT;
        googleMapCommunicator = new GoogleMapCommunicator(context, type);
        serviceConnection.startService(context, new ServiceConnectionListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mxtide_map_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupActionBarTitle();

        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MapReadyListener());
    }

    @Override
    public void onPause() {
        if (googleMapCommunicator.getGoogleMap() != null) {
            LatLng latLng = googleMapCommunicator.getGoogleMap().getCameraPosition().target;
            float zoom = googleMapCommunicator.getGoogleMap().getCameraPosition().zoom;
            mxPreferences.setGoogleMapLocation(latLng);
            mxPreferences.setGoogleMapZoom(zoom);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        context.unbindService(serviceConnection);
        super.onDestroy();
    }


    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region IMPLEMENTATION  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public MXMainFragmentId getFragmentId() {
        return (MXMainFragmentId) getArguments().getSerializable(MXMainFragmentId.class.getSimpleName());
    }

    @Override
    public void invalidate() {
        //nothing to do
    }

    @Override
    public void setupActionBarTitle() {
        if (getActivity() != null) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                if (getFragmentId() == MXMainFragmentId.MAP_FRAGMENT_TIDES) {
                    actionBar.setSubtitle(getString(R.string.tide_station_map_subtitle));
                } else {
                    actionBar.setSubtitle(getString(R.string.current_station_map_subtitle));
                }
            }
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private class ServiceConnectionListener implements HarmonicsServiceConnection.ConnectionListener {
        @Override
        public void onServiceLoaded() {
            googleMapCommunicator.setHarmonicsDatabaseService(serviceConnection.getHarmonicsDatabaseService());
        }

        @Override
        public void onServiceLoadError() {
            Log.e(TAG, "onServiceLoadError");
        }

        @Override
        public void onServiceDisconnected() {
            googleMapCommunicator.setHarmonicsDatabaseService(null);
        }
    }

    private class MapReadyListener implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap gMap) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mxPreferences.getGoogleMapLocation(),
                    mxPreferences.getGoogleMapZoom());
            gMap.moveCamera(cu);
            googleMapCommunicator.setGoogleMap(gMap);
            gMap.setOnCameraChangeListener(googleMapCommunicator);
            gMap.setInfoWindowAdapter(googleMapCommunicator);
            gMap.setOnInfoWindowClickListener(googleMapCommunicator);
            gMap.setMyLocationEnabled(true);
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
