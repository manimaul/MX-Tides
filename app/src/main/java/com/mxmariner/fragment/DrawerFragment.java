package com.mxmariner.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxmariner.bus.DrawerMenuEvent;
import com.mxmariner.bus.EventBus;
import com.mxmariner.tides.R;

public class DrawerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.drawer_layout, container, false);
        v.findViewById(R.id.drawer_layout_close_tide_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.CloseTideStations);
                    }
                });
        v.findViewById(R.id.drawer_layout_close_current_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.CloseCurrentStations);
                    }
                });
        v.findViewById(R.id.drawer_layout_map_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.Map);
                    }
                });
        v.findViewById(R.id.drawer_layout_settings_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.Settings);
                    }
                });
        v.findViewById(R.id.drawer_layout_harmonics_info_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.Harmonics);
                    }
                });
        v.findViewById(R.id.drawer_layout_about_tv)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getInstance()
                                .post(DrawerMenuEvent.About);
                    }
                });
        return v;
    }
}
