package com.mxmariner.activity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.mxmariner.bus.DrawerMenuEvent;
import com.mxmariner.bus.EventBus;
import com.mxmariner.fragment.DrawerSettingsFragment;
import com.mxmariner.fragment.FragmentId;
import com.mxmariner.fragment.MXFragment;
import com.mxmariner.fragment.StationCardRecyclerFragment;
import com.mxmariner.tides.R;
import com.squareup.otto.Subscribe;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    //region FIELDS ********************************************************************************

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentId currentFragmentId = null;
    private FragmentId pendingId = null;

    //endregion ************************************************************************************

    //region LIFE CYCLE ****************************************************************************

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerListener();

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        EventBus.getInstance().register(this);

        navigateToFragmentWithId(FragmentId.StationCardRecyclerFragmentTides);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

    public void navigateToFragmentWithId(FragmentId fragmentId) {
        if (currentFragmentId != fragmentId) {
            MXFragment mxFragment = null;
            switch (fragmentId) {
                case StationCardRecyclerFragmentTides: {
                    mxFragment = StationCardRecyclerFragment.createFragment(fragmentId);
                    break;
                }

                case StationCardRecyclerFragmentCurrents: {
                    mxFragment = StationCardRecyclerFragment.createFragment(fragmentId);
                    break;
                }
            }

            if (mxFragment != null) {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                                R.animator.slide_in_right, R.animator.slide_out_left)
                        .replace(R.id.activity_main_fragment_container, mxFragment)
                        .commit();
                currentFragmentId = fragmentId;
            } else {
                Log.e(TAG, "navigateToFragmentWithId() fragment was null!");
            }
        } else {
            Log.d(TAG, "navigateToFragmentWithId() already at target fragment");
        }
    }

    //endregion ************************************************************************************

    //region LISTENERS *****************************************************************************

    private class ActionBarDrawerListener extends ActionBarDrawerToggle {
        private ActionBarDrawerListener() {
            super(MainActivity.this, drawerLayout, R.string.drawer_open,
                    R.string.drawer_close);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            if (pendingId != null) {
                navigateToFragmentWithId(pendingId);
                pendingId = null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion ************************************************************************************

    //region EVENTS  *******************************************************************************

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe
    public void onDrawerMenuEvent(DrawerMenuEvent event) {
        switch (event) {
            case CloseTideStations: {
                pendingId = FragmentId.StationCardRecyclerFragmentTides;
                drawerLayout.closeDrawer(Gravity.START);
                break;
            }
            case CloseCurrentStations: {
                pendingId = FragmentId.StationCardRecyclerFragmentCurrents;
                drawerLayout.closeDrawer(Gravity.START);
                break;
            }
            case Map: {
                drawerLayout.closeDrawer(Gravity.START);
                break;
            }
            case Harmonics: {
                drawerLayout.closeDrawer(Gravity.START);
                break;
            }
            case Settings: {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right,
                                R.animator.slide_in_right, R.animator.slide_out_left)
                        .replace(R.id.left_drawer, new DrawerSettingsFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case SettingsDone: {
                getFragmentManager()
                        .popBackStack();
                break;
            }
            case About: {
                drawerLayout.closeDrawer(Gravity.START);
                break;
            }
        }
    }

    //endregion ************************************************************************************

}
