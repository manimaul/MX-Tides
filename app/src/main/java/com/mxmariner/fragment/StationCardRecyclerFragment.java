package com.mxmariner.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mxmariner.andxtidelib.IHarmonicsDatabaseService;
import com.mxmariner.andxtidelib.MXLatLng;
import com.mxmariner.andxtidelib.remote.RemoteStation;
import com.mxmariner.andxtidelib.remote.RemoteStationData;
import com.mxmariner.andxtidelib.remote.StationType;
import com.mxmariner.tides.R;
import com.mxmariner.util.HarmonicsServiceConnection;
import com.mxmariner.util.LocationUtils;
import com.mxmariner.util.MXPreferences;
import com.mxmariner.viewcomponent.StationAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StationCardRecyclerFragment extends MXMainFragment {

    //region CLASS VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final String TAG = StationCardRecyclerFragment.class.getSimpleName();

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CLASS METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region FIELDS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Context context;
    private MXPreferences mxPreferences;
    private StationAdapter stationAdapter = new StationAdapter();
    private HarmonicsServiceConnection serviceConnection = new HarmonicsServiceConnection();
    private int stationCardCount;

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static StationCardRecyclerFragment createFragment(MXMainFragmentId fragmentId, Bundle args) {
        StationCardRecyclerFragment fragment = new StationCardRecyclerFragment();
        args = args == null ? new Bundle() : args ;
        args.putSerializable(MXMainFragmentId.class.getSimpleName(), fragmentId);
        fragment.setArguments(args);
        return fragment;
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region ACCESSORS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PRIVATE METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void getStationDataAsync(final IHarmonicsDatabaseService service, final StationType type) {
        AsyncTask<Void, Void, List<RemoteStationData>> task = new AsyncTask<Void, Void, List<RemoteStationData>>() {
            @Override
            protected List<RemoteStationData> doInBackground(Void... params) {
                Log.d(TAG, "getStationDataAsync()");
                MXLatLng position = LocationUtils.getLastKnownLocation(context);
                List<RemoteStationData> stationDatas = null;
                try {
                    List<RemoteStation> remoteStations = service.getClosestStations(type, position.getLatitude(), position.getLongitude(), stationCardCount);
                    stationDatas = new ArrayList<>(remoteStations.size());
                    long epoch = Calendar.getInstance().getTime().getTime() / 1000;
                    RemoteStationData rsd;
                    for (RemoteStation rs : remoteStations) {
                        int options = RemoteStationData.REQUEST_OPTION_PREDICTION;
                        rsd = service.getDataForTime(rs.getStationId(), epoch, options);
                        if (rsd != null) {
                            stationDatas.add(rsd);
                        }
                    }
                    Log.d(TAG, "sorting station data");
                    Collections.sort(stationDatas, new StationSorter(position));
                    Log.d(TAG, "returning station data");
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException", e);
                }
                return stationDatas;
            }

            @Override
            protected void onPostExecute(List<RemoteStationData> results) {
                super.onPostExecute(results);
                if (results != null) {

                    if (recyclerView != null && progressBar != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        stationAdapter.setStationDataAndInvalidate(results);
                    }
                } else {
                    //todo: handle error ui
                }
            }
        };
        task.execute();
    }

    @Override
    public void setupActionBarTitle() {
        if (getActivity() != null) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                int id = getStationType() == StationType.STATION_TYPE_TIDE ? R.string.closest_tide_stations : R.string.closest_current_stations;
                int numStations = mxPreferences.getNumberOfStationCardsPref();
                actionBar.setSubtitle(numStations + " " + getString(id));
            }
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region PUBLIC METHODS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region INNER CLASSES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private StationType getStationType() {
        if (getFragmentId() == MXMainFragmentId.STATION_CARD_RECYCLER_FRAGMENT_TIDES) {
            return StationType.STATION_TYPE_TIDE;
        } else {
            return StationType.STATION_TYPE_CURRENT;
        }
    }

    private class StationSorter implements Comparator<RemoteStationData> {

        private final double lat;
        private final double lng;

        StationSorter(MXLatLng position) {
            if (position == null) {
                lat = 0;
                lng = 0;
            } else {
                lat = position.getLatitude();
                lng = position.getLongitude();
            }
        }

        @Override
        public int compare(RemoteStationData lhs, RemoteStationData rhs) {

            int lhsDistance = MXLatLng.distanceToPoint(lat, lng, lhs.getLatitude(), lhs.getLongitude());
            int rhsDistance = MXLatLng.distanceToPoint(lat, lng, rhs.getLatitude(), rhs.getLongitude());

            if (lhsDistance == rhsDistance) {
                return 0;
            }

            return lhsDistance < rhsDistance ? -1 : 1;
        }
    }

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

        context = getActivity().getApplicationContext();
        mxPreferences = new MXPreferences(context);
        stationCardCount = mxPreferences.getNumberOfStationCardsPref();

        serviceConnection.startService(context, new ServiceConnectionListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.station_card_recycler_fragment_layout, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.station_card_recycler_fragment_rv);
        progressBar = (ProgressBar) v.findViewById(R.id.station_card_recycler_fragment_pb);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(stationAdapter);
        setupActionBarTitle();
        return v;
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
        if (serviceConnection.getHarmonicsDatabaseService() != null && mxPreferences.getNumberOfStationCardsPref() != stationCardCount) {
            stationCardCount = mxPreferences.getNumberOfStationCardsPref();
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getStationDataAsync(serviceConnection.getHarmonicsDatabaseService(), getStationType());
            setupActionBarTitle();
        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //region LISTENERS  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private class ServiceConnectionListener implements HarmonicsServiceConnection.ConnectionListener {
        @Override
        public void onServiceLoaded() {
            if (serviceConnection.getHarmonicsDatabaseService() != null) {
                getStationDataAsync(serviceConnection.getHarmonicsDatabaseService(), getStationType());
            }
        }

        @Override
        public void onServiceLoadError() {

        }

        @Override
        public void onServiceDisconnected() {

        }
    }

    //endregion ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


}
