package com.mxmariner.tides;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.mxmariner.andxtidelib.IHarmonicsDatabaseService;
import com.mxmariner.andxtidelib.IRemoteServiceCallback;
import com.mxmariner.andxtidelib.IRemoteStationData;
import com.mxmariner.util.LocationUtils;
import com.mxmariner.andxtidelib.MXLatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyServiceConnection mServiceConnection = new MyServiceConnection();
    private IHarmonicsDatabaseService harmonicsDatabaseService;

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

        Intent serviceIntent = new Intent("com.mxmariner.andxtidelib.HarmonicsDatabaseService");
        serviceIntent.setPackage("com.mxmariner.tides");
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void onHarmonicsDatabaseOpened(final IHarmonicsDatabaseService service) {
        try {
            service.loadDatabaseAsync(new ServiceCallback(service));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    
    private class ServiceCallback extends IRemoteServiceCallback.Stub {

        private final IHarmonicsDatabaseService service;

        private ServiceCallback(IHarmonicsDatabaseService service) {
            this.service = service;
        }

        @Override
        public void onComplete(int result) throws RemoteException {
            MXLatLng position = LocationUtils.getLastKnownLocation(MainActivity.this);
            long[] ids = service.getClosestStations(position.getLatitude(), position.getLongitude(), 10);
            ArrayList<IRemoteStationData> stationDatas = new ArrayList<>(ids.length);
            long epoch = Calendar.getInstance().getTime().getTime() / 1000;
            for (long id : ids) {
                stationDatas.add(service.getDataForTime(id, epoch));
            }
            StationAdapter adapter = new StationAdapter(stationDatas, position);
            recyclerView.setAdapter(adapter);
        }
    }
    
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            harmonicsDatabaseService = IHarmonicsDatabaseService.Stub.asInterface(service);
            onHarmonicsDatabaseOpened(harmonicsDatabaseService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            harmonicsDatabaseService = null;
        }
    }
    

}
