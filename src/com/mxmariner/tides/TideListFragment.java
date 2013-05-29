package com.mxmariner.tides;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mxmariner.units.UnitConversions;

public class TideListFragment extends ListFragment {
	public static final String TAG = "TIDES";
	private final String distTmpl = "Distance: %s miles";
	private boolean isEmpty;
	
	@Override
	public void onResume() {
		final MainActivity ma = ((MainActivity) getActivity());
		final ArrayList<Station> subList = ma.getStationSubList(Station.TYPE_TIDE);
		Collections.sort(subList, SorterFactory.getStationDistanceSorter());
		final StationArrayAdapter adapter = new StationArrayAdapter(ma, subList);
		if (subList.size() == 0) {
			isEmpty = true;
			String[] items = {"No stations in defined area."};
			setListAdapter(new ArrayAdapter<String>(ma, android.R.layout.simple_list_item_1, items));
		} else {
			isEmpty = false;
			setListAdapter(adapter);
		}
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (isEmpty)
			return;
		
		final MainActivity ma = ((MainActivity) getActivity());
		ma.setSelectedStation(ma.getTideStationSubList().get(position));
		ma.goToStationTab();
	}
	
	private class StationArrayAdapter extends ArrayAdapter<Station> {

		public StationArrayAdapter(Context context, List<Station> objects) {
			super(context, android.R.layout.simple_list_item_2, objects);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// row inflation
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(android.R.layout.simple_list_item_2,
						parent, false);
			}
			
			// get item
            Station station = getItem(position);
			
			TextView t1 = (TextView) row.findViewById(android.R.id.text1);
			TextView t2 = (TextView) row.findViewById(android.R.id.text2);
			t1.setText(station.getName());
			t2.setText(String.format(distTmpl, UnitConversions.MeToMi(station.getDistance())));
			return row;
		}
		
	}

}
