package com.mxmariner.tides;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.mxmariner.andxtidelib.IRemoteStationData;
import com.mxmariner.andxtidelib.MXLatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    public static final String TAG = StationAdapter.class.getSimpleName();

    private ArrayList<IRemoteStationData> stations;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(StationCard v) {
            super(v);
        }
    }

    /**
     *
     * @param stationList list of station data objects to display
     * @param sortPosition position to sort list with by distance to
     */
    public StationAdapter(ArrayList<IRemoteStationData> stationList, MXLatLng sortPosition) {
        stations = stationList;
        Collections.sort(stations, new StationSorter(sortPosition));
    }

    /**
     *
     * @param stationList list of station data objects to display
     */
    public StationAdapter(ArrayList<IRemoteStationData> stationList) {
        stations = stationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(new StationCard(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final StationCard card = (StationCard) viewHolder.itemView;
        IRemoteStationData remoteStationData = stations.get(i);
        card.applyStationData(remoteStationData);
    }


    @Override
    public int getItemCount() {
        return stations.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        final StationCard card = (StationCard) holder.itemView;
        card.recycleView();
        Log.d(TAG, "--------------onViewRecycled");
        Log.d(TAG, "getPosition" + holder.getPosition());
        Log.d(TAG, "getOldPosition" + holder.getOldPosition());
        super.onViewRecycled(holder);
    }

    private class StationSorter implements Comparator<IRemoteStationData> {

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
        public int compare(IRemoteStationData lhs, IRemoteStationData rhs) {

            try {
                int lhsDistance = MXLatLng.distanceToPoint(lat, lng, lhs.getLatitude(), lhs.getLongitude());
                int rhsDistance = MXLatLng.distanceToPoint(lat, lng, rhs.getLatitude(), rhs.getLongitude());

                if (lhsDistance == rhsDistance) {
                    return 0;
                }
                
                return lhsDistance < rhsDistance ? -1 : 1;
            } catch (RemoteException e) {
                MXLogger.e(TAG, "StationSorter RemoteException", e);
            }
            
            return 0;
        }
    }

}
