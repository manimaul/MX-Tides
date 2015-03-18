package com.mxmariner.drawer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mxmariner.bus.DrawerMenuEvent;
import com.mxmariner.bus.EventBus;
import com.mxmariner.fragment.MXMainFragmentId;
import com.mxmariner.tides.R;
import com.mxmariner.util.MXPreferences;

public class DrawerFragment extends Fragment {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private ClickListener clickListener = new ClickListener();
    private MXPreferences mxPreferences;
    private TextView closeTideStations;
    private TextView closeCurrentStations;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void invalidate() {
        if (closeCurrentStations != null && closeTideStations != null) {
            int numStations = mxPreferences.getNumberOfStationCardsPref();
            closeTideStations.setText(numStations + " " + getString(R.string.closest_tide_stations));
            closeCurrentStations.setText(numStations + " " + getString(R.string.closest_current_stations));
        }
    }

//    public void onChangeStationType(StationType type) {
//        if (type == StationType.STATION_TYPE_TIDE) {
//            radioGroup.check();
//        } else {
//            currentCheckBox.setChecked(true);
//            tideCheckBox.setChecked(false);
//        }
//        mxPreferences.setStationType(type);
//    }

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
        mxPreferences = new MXPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.drawer_layout, container, false);

        closeTideStations = (TextView) v.findViewById(R.id.drawer_layout_close_tide);
        closeCurrentStations = (TextView) v.findViewById(R.id.drawer_layout_close_current);

        invalidate();

        int[] ids = {R.id.drawer_layout_close_tide, R.id.drawer_layout_close_current,
                R.id.drawer_layout_tide_map, R.id.drawer_layout_current_map,
                R.id.drawer_layout_settings_tv, R.id.drawer_layout_harmonics_info_tv,
                R.id.drawer_layout_about_tv};

        for (int id : ids) {
            v.findViewById(id).setOnClickListener(clickListener);
        }

        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.drawer_layout_fragment_radio_group);

        MXPreferences mxPreferences = new MXPreferences(v.getContext());
        MXMainFragmentId id = mxPreferences.getMainFragmentId();
        if (id == MXMainFragmentId.STATION_CARD_RECYCLER_FRAGMENT_TIDES) {
            radioGroup.check(R.id.drawer_layout_close_tide);
        } else if (id == MXMainFragmentId.STATION_CARD_RECYCLER_FRAGMENT_CURRENTS) {
            radioGroup.check(R.id.drawer_layout_close_current);
        } else if (id == MXMainFragmentId.MAP_FRAGMENT_TIDES) {
            radioGroup.check(R.id.drawer_layout_tide_map);
        } else if (id == MXMainFragmentId.MAP_FRAGMENT_CURRENTS) {
            radioGroup.check(R.id.drawer_layout_current_map);
        }

        return v;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region IMPLEMENTATION  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Object event = null;
            switch (v.getId()) {
                case R.id.drawer_layout_close_tide:
                    event = DrawerMenuEvent.CLOSE_TIDE_STATIONS;
                    break;
                case R.id.drawer_layout_close_current:
                    event = DrawerMenuEvent.CLOSE_CURRENT_STATIONS;
                    break;
                case R.id.drawer_layout_tide_map:
                    event = DrawerMenuEvent.MAP_TIDE;
                    break;
                case R.id.drawer_layout_current_map:
                    event = DrawerMenuEvent.MAP_CURRENT;
                    break;
                case R.id.drawer_layout_settings_tv:
                    event = DrawerMenuEvent.SETTINGS;
                    break;
                case R.id.drawer_layout_harmonics_info_tv:
                    event = DrawerMenuEvent.HARMONICS;
                    break;
                case R.id.drawer_layout_about_tv:
                    event = DrawerMenuEvent.ABOUT;
                    break;
            }

            if (event != null) {
                EventBus.getInstance()
                        .post(event);
            }
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
