package com.mxmariner.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxmariner.bus.DrawerMenuEvent;
import com.mxmariner.bus.EventBus;
import com.mxmariner.tides.R;

public class DrawerSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.drawer_layout_settings, container, false);
        View doneTv = v.findViewById(R.id.drawer_layout_settings_done_tv);
        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getInstance()
                        .post(DrawerMenuEvent.SettingsDone);
            }
        });
        return v;
    }

}
