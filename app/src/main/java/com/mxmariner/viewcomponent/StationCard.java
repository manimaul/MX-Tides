package com.mxmariner.viewcomponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.tides.R;

public class StationCard extends CardView {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String TAG = StationCard.class.getSimpleName();
    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private TextView nameTv;
    private TextView dateTv;
    private TextView predictionTv;
    private ImageView clockIv;
    private AsyncTask<Void, Void, TransitionDrawable> clockIvTask;
    private View mapCover;
    private GoogleMap googleMap;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public StationCard(Context context) {
        super(context);
        init();
    }

    public StationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.station_card, this);
        nameTv = (TextView) findViewById(R.id.station_card_name);
        dateTv = (TextView) findViewById(R.id.station_card_datetime);
        predictionTv = (TextView) findViewById(R.id.station_card_prediction);
        clockIv = (ImageView) findViewById(R.id.station_card_clock_iv);
        mapCover = findViewById(R.id.map_view_cover);
        MapView mapView = (MapView) findViewById(R.id.map_view);

        if (MapsInitializer.initialize(getContext()) == 0) {
            mapView.onCreate(new Bundle());
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        } else {
            Log.w(TAG, "no google play services");
            mapView.setVisibility(View.GONE);
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void loadClockAsync(RemoteStationData remoteStationData) {
        final String svgString = remoteStationData.getOptionalClockSvg();
        if (svgString != null) {
            clockIvTask = new AsyncTask<Void, Void, TransitionDrawable>() {
                @Override
                protected TransitionDrawable doInBackground(Void... params) {
                    try {
                        SVG svg = SVG.getFromString(svgString);
                        if (svg.getDocumentWidth() != -1) {
                            int width = (int) (svg.getDocumentWidth() * getResources().getDisplayMetrics().scaledDensity);
                            int height = (int) (svg.getDocumentHeight() * getResources().getDisplayMetrics().scaledDensity);
                            svg.setDocumentHeight(height);
                            svg.setDocumentWidth(width);

                            Bitmap newBM = Bitmap.createBitmap(width, height,
                                    Bitmap.Config.ARGB_8888);
                            Canvas bmcanvas = new Canvas(newBM);
                            // Clear background to white
                            bmcanvas.drawRGB(255, 255, 255);
                            // Render our document onto our canvas
                            svg.renderToCanvas(bmcanvas);

                            return new TransitionDrawable(new Drawable[]{
                                    new ColorDrawable(Color.TRANSPARENT),
                                    new BitmapDrawable(getResources(), newBM)
                            });
                        }
                    } catch (SVGParseException e) {
                        Log.e(TAG, "", e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(TransitionDrawable drawable) {
                    super.onPostExecute(drawable);
                    if (drawable != null) {
                        clockIv.setImageDrawable(drawable);
                        drawable.startTransition(500);
                    }
                }
            };
            clockIvTask.execute();
        }
    }

    private int getIconResourceId(StationType stationType) {
        if (stationType == StationType.STATION_TYPE_TIDE) {
            return R.drawable.tidept;
        } else {
            return R.drawable.currentpt;
        }
    }

    private void updateMapView(RemoteStationData remoteStationData) {
        if (remoteStationData != null && googleMap != null) {
            LatLng position = new LatLng(remoteStationData.getLatitude(), remoteStationData.getLongitude());
            int iconId = getIconResourceId(remoteStationData.getStationType());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 7));
            googleMap.addMarker(new MarkerOptions()
                    .position(position))
                    .setIcon(BitmapDescriptorFactory.fromResource(iconId));
            mapCover.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start();
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void applyStationData(final RemoteStationData data) {
        nameTv.setText(data.getName());
        dateTv.setText(data.getDataTimeStamp());
        predictionTv.setText(data.getOptionalPrediction());
        updateMapView(data);
        loadClockAsync(data);
    }

    public void recycleView() {
        nameTv.setText(null);
        dateTv.setText(null);
        predictionTv.setText(null);
        if (clockIvTask != null) {
            clockIvTask.cancel(true);
            clockIvTask = null;
        }
        clockIv.setImageDrawable(null);
        mapCover.setAlpha(1f);
        if (googleMap != null) {
            googleMap.clear();
        }
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

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
