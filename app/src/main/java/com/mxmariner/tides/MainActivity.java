package com.mxmariner.tides;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.mxmariner.andxtidelib.HarmonicsDatabase;
import com.mxmariner.andxtidelib.MXLatLng;
import com.mxmariner.andxtidelib.Station;
import com.mxmariner.util.MXTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity implements HarmonicsDatabase.IHarmonicsDatabaseCallback {
    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.activity_main_loading);

        recyclerView.setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);

        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        String tcdName = "harmonics-dwf-20141224-free.tcd";
        File tcd = new File(getFilesDir(), tcdName);

        if (MXTools.copyAssetFile(getApplicationContext(), "harmonics/" + tcdName, tcd)) {
            HarmonicsDatabase.createDatabaseAsync(tcd, this);
        }
    }

    @Override
    public void onInitiated(HarmonicsDatabase database) {
        ArrayList<Station> stations = database.getTideStations();
        Collections.sort(stations, new StationSorter());
        StationAdapter adapter = new StationAdapter(stations);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private MXLatLng getLastKnownLocation() {
        LocationManager pLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Location gpsLocation = getLastKnownLocation(pLocationManager, LocationManager.GPS_PROVIDER);
        final Location networkLocation = getLastKnownLocation(pLocationManager, LocationManager.NETWORK_PROVIDER);
        if (gpsLocation == null) {
            return new MXLatLng(networkLocation);
        } else if (networkLocation == null) {
            return new MXLatLng(gpsLocation);
        } else {
            // both are non-null - use the most recent (+ delay for GPS)
            if (networkLocation.getTime() > gpsLocation.getTime() + 3000) {
                return new MXLatLng(networkLocation);
            } else {
                return new MXLatLng(gpsLocation);
            }
        }
    }

    private Location getLastKnownLocation(LocationManager pLocationManager, String pProvider) {
        try {
            if (!pLocationManager.isProviderEnabled(pProvider)) {
                return null;
            }
        } catch (final IllegalArgumentException e) {
            return null;
        }
        return pLocationManager.getLastKnownLocation(pProvider);
    }

    class StationSorter implements Comparator<Station> {
        MXLatLng position = getLastKnownLocation();

        @Override
        public int compare(Station lhs, Station rhs) {
            if (position == null) {
                return 0;
            }
            int lhsDistance = lhs.getPosition().distanceToPoint(position);
            int rhsDistance = rhs.getPosition().distanceToPoint(position);

            if (lhsDistance == rhsDistance) {
                return 0;
            }
            return lhsDistance < rhsDistance ? -1 : 1;
        }
    }

}
