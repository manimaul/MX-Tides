package com.mxmariner.tides;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.mxmariner.andxtidelib.IRemoteStationData;

import java.util.ArrayList;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    public static final String TAG = StationAdapter.class.getSimpleName();

    private ArrayList<IRemoteStationData> stations;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(StationCard v) {
            super(v);
        }
    }

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


}
