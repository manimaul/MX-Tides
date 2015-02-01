package com.mxmariner.tides;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mxmariner.andxtidelib.IStationData;
import com.mxmariner.andxtidelib.Station;

import java.util.ArrayList;
import java.util.Calendar;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    public static final String TAG = StationAdapter.class.getSimpleName();

    private ArrayList<Station> stations;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(StationCard v) {
            super(v);
        }
    }

    public StationAdapter(ArrayList<Station> stationList) {
        stations = stationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(new StationCard(viewGroup.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final StationCard card = (StationCard) viewHolder.itemView;
        card.setCurrentIndex(i);
        final IStationData data = stations.get(i).getDataForTime(Calendar.getInstance().getTime());
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                data.preLoad();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                card.applyStationData(data, i);
            }
        };
        task.execute();
    }


    @Override
    public int getItemCount() {
        return stations.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
