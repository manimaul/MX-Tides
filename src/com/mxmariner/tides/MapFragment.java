package com.mxmariner.tides;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFragment extends Fragment {
	public static final String TAG = "MAP";
	
	/*
	 * Fragment Lifecycle
     * - 1 onAttach(Activity) called once the fragment is associated with its activity.
     * - 2 onCreate(Bundle) called to do initial creation of the fragment.
     * - 3 onCreateView(LayoutInflater, ViewGroup, Bundle) creates and returns the view hierarchy associated with the fragment.
     * - 4 onActivityCreated(Bundle) tells the fragment that its activity has completed its own Activity.onCreate().
     * - 5 onViewStateRestored(Bundle) tells the fragment that all of the saved state of its view hierarchy has been restored.
     * - 6 onStart() makes the fragment visible to the user (based on its containing activity being started).
     * - 7 onResume() makes the fragment interacting with the user (based on its containing activity being resumed). 
	 * */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View mView = inflater.inflate(R.layout.myview, container, false);
		return mView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final MainActivity ma = ((MainActivity) getActivity());
		ma.setUseMapBox(true);
		ma.getTideStationSubList().clear();
		ma.getCurrStationSubList().clear();
	}
	
	@Override
	public void onPause() {
		final MainActivity ma = ((MainActivity) getActivity());
		final MyMapView mv = ma.getMapView();
		ma.getPrefEditor().putInt("latE6", mv.getMapCenter().getLatitudeE6());
		ma.getPrefEditor().putInt("lngE6", mv.getMapCenter().getLongitudeE6());
		ma.getPrefEditor().putInt("zoom", mv.getZoomLevel());
		ma.getPrefEditor().commit();
		super.onPause();
	}
	
}
