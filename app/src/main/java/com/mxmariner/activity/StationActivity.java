package com.mxmariner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.tides.R;
import com.mxmariner.util.HarmonicsServiceConnection;
import com.mxmariner.viewcomponent.TextViewList;

public class StationActivity extends Activity {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final String TAG = StationActivity.class.getSimpleName();
    private static final String STATION_ID = "STATION_ID";

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static void startWithStationId(Context packageContext, Long stationId) {
        packageContext.startActivity(getStartIntent(packageContext, stationId));
    }

    public static Intent getStartIntent(Context packageContext, Long stationId) {
        Intent intent = new Intent(packageContext, StationActivity.class);
        intent.putExtra(STATION_ID, stationId);
        return intent;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    private Long stationId;

    private TextView nameTv;
    private TextView dateTv;
    private ImageView graphIv;
    private ImageView clockIv;
    private TextView predictionTv;
    private TextViewList detailsLayout;
    private GoogleMap googleMap;
    private RemoteStationData remoteStationData;
    private HarmonicsServiceConnection serviceConnection = new HarmonicsServiceConnection();

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void updateMapView() {
        if (remoteStationData != null && googleMap != null) {
            LatLng position = new LatLng(remoteStationData.getLatitude(), remoteStationData.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 7));
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(remoteStationData.getName()));
        }
    }

    private void showErrorAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Whoops!")
                .setMessage("Error getting station data :(")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private Bitmap svgFromString(String svgString) {
        try {
            SVG svg = SVG.getFromString(svgString);
            if (svg.getDocumentWidth() != -1) {
                Bitmap newBM = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                        (int) Math.ceil(svg.getDocumentHeight()),
                        Bitmap.Config.ARGB_8888);
                Canvas bmcanvas = new Canvas(newBM);
                // Clear background to white
                bmcanvas.drawRGB(255, 255, 255);
                // Render our document onto our canvas
                svg.renderToCanvas(bmcanvas);
                return newBM;
            }
        } catch (SVGParseException e) {
            Log.e(TAG, "", e);
        }

        return null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station);

        MapView mapView = (MapView) findViewById(R.id.map_view);

        if (MapsInitializer.initialize(this) == 0) {
            mapView.onCreate(savedInstanceState);
            googleMap = mapView.getMap();
        } else {
            Log.w(TAG, "no google play services");
            mapView.setVisibility(View.GONE);
        }


        nameTv = (TextView) findViewById(R.id.activity_station_name);
        dateTv = (TextView) findViewById(R.id.activity_station_datetime);
        graphIv = (ImageView) findViewById(R.id.activity_station_graph_iv);
        clockIv = (ImageView) findViewById(R.id.activity_station_clock_iv);
        predictionTv = (TextView) findViewById(R.id.activity_station_prediction);
        detailsLayout = (TextViewList) findViewById(R.id.activity_station_details_container);
        stationId = getIntent().getLongExtra(STATION_ID, 0l);
        if (stationId.equals(0l)) {
            showErrorAlert();
        }

        serviceConnection.startService(this, new ServiceConnectionListener());
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region IMPLEMENTATION  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private class ServiceConnectionListener implements HarmonicsServiceConnection.ConnectionListener {
        @Override
        public void onServiceLoaded() {
//            if (serviceConnection.getHarmonicsDatabaseService() != null) {
//                long epoch = Calendar.getInstance().getTime().getTime() / 1000;
//                try {
//                    remoteStationData = serviceConnection.getHarmonicsDatabaseService().getDataForTime(stationId, epoch);
//                    nameTv.setText(remoteStationData.getName());
//                    dateTv.setText(remoteStationData.getDataTimeStamp());
//                    predictionTv.setText(remoteStationData.getOptionalPrediction());
//                    detailsLayout.addTextViewsWithStrings(remoteStationData.getOptionalPlainData());
//                    String svg = remoteStationData.getOptionalGraphSvg();
//                    if (svg != null) {
//                        graphIv.setImageBitmap(svgFromString(svg));
//                    }
//                    svg = remoteStationData.getOptionalClockSvg();
//                    if (svg != null) {
//                        clockIv.setImageBitmap(svgFromString(svg));
//                    }
//
//                    updateMapView();
//                } catch (RemoteException e) {
//                    Log.e(TAG, "onServiceLoaded()", e);
//                }
//            }
        }

        @Override
        public void onServiceLoadError() {
            Log.e(TAG, "onServiceLoadError()");
            showErrorAlert();
        }

        @Override
        public void onServiceDisconnected() {
            Log.d(TAG, "onServiceDisconnected()");
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
}
