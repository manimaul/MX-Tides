package com.mxmariner.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.mxmariner.fragment.MXMainFragmentId;
import com.mxmariner.tides.MXLogger;
import com.mxmariner.tides.R;

public class MXPreferences {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String TAG = MXPreferences.class.getSimpleName();

    private static final String PREFERENCES_KEY = "PREFERENCES_KEY";
    private static final String PREF_KEY_NUM_STATION_CARDS = "PREF_NUM_STATION_CARDS";
    private static final String PREF_KEY_UNITS = "PREF_KEY_UNITS";
    private static final String PREF_KEY_MAP_ZOOM = "PREF_KEY_MAP_ZOOM";
    private static final String PREF_KEY_MAP_LAT = "PREF_KEY_MAP_LAT";
    private static final String PREF_KEY_MAP_LNG = "PREF_KEY_MAP_LNG";
    private static final String PREF_KEY_FRAGMENT_ID = "PREF_KEY_FRAGMENT_ID";

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public MXPreferences(Context context) {
        this.context = context;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int getNumberOfStationCardsPref() {
        int defVal = Integer.parseInt(context.getResources().getString(R.string.station_count_default));
        return getSharedPreferences().getInt(PREF_KEY_NUM_STATION_CARDS, defVal);
    }

    public void setNumberOfStationCardsPref(Object value) {
        try {
            Integer intValue = Integer.parseInt(String.valueOf(value));
            getEditor()
                    .putInt(PREF_KEY_NUM_STATION_CARDS, intValue)
                    .apply();
        } catch (Exception e) {
            MXLogger.e(TAG, "setNumberOfStationCardsPref(" + value + ")", e);
        }
    }

    public String getUnitsOfMeasurePref() {
        String defVal = context.getString(R.string.unit_option_statute);
        return getSharedPreferences().getString(PREF_KEY_UNITS, defVal);
    }

    public void setUnitsOfMeasuerPref(Object value) {
        getEditor()
                .putString(PREF_KEY_UNITS, String.valueOf(value))
                .apply();

    }

    public void setMainFragmentId(MXMainFragmentId type) {
        getEditor().putString(PREF_KEY_FRAGMENT_ID, type.name())
                .apply();
    }

    public MXMainFragmentId getMainFragmentId() {
        String id = getSharedPreferences().getString(PREF_KEY_FRAGMENT_ID,
                MXMainFragmentId.defaultId().name());

        return MXMainFragmentId.getIdFromString(id);
    }

    public float getGoogleMapZoom() {
        return getSharedPreferences().getFloat(PREF_KEY_MAP_ZOOM, 2f);
    }

    public void setGoogleMapZoom(float zoom) {
        getEditor()
                .putFloat(PREF_KEY_MAP_ZOOM, zoom)
                .apply();
    }

    public LatLng getGoogleMapLocation() {
        double lat = getSharedPreferences().getFloat(PREF_KEY_MAP_LAT, 0f);
        double lng = getSharedPreferences().getFloat(PREF_KEY_MAP_LNG, 0f);
        return new LatLng(lat, lng);
    }

    public void setGoogleMapLocation(LatLng location) {
        getEditor()
                .putFloat(PREF_KEY_MAP_LAT, (float) location.latitude)
                .putFloat(PREF_KEY_MAP_LNG, (float) location.longitude)
                .apply();

    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSharedPreferences().edit();
        }
        return editor;
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


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
