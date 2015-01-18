package com.mxmariner.tides;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.LocationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toolbar;

import com.mxmariner.andxtidelib.XtideJni;
import com.mxmariner.util.Constants;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";

	private Logger logger = null;
	private final boolean debugon = true;
	private final XtideJni xtideJni = new XtideJni();
	private final ArrayList<Station> tideStations = new ArrayList<Station>();
	private final ArrayList<Station> currentStations = new ArrayList<Station>();
	private ArrayList<Station> subListTides = new ArrayList<Station>();
	private ArrayList<Station> subListCurrents = new ArrayList<Station>();
	private LocationManager locationManager;
	private Location location;
	private Station selectedStation;
	private MyMapView mMapView;
	private SharedPreferences prefs;
	private SharedPreferences.Editor prefeditor;
	private BoundingBoxE6 mapBox;
	private boolean useMapBox = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.noticetitle);
		builder.setMessage(R.string.notice);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		} );
		final AlertDialog notice = builder.create();
		notice.show();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		location = LocationUtils.getLastKnownLocation(locationManager);

		if (debugon) {
			logger = LoggerFactory.getLogger(Preparations.class);
			logger.info("debug is on!");
		}

		Preparations preps = new Preparations(this);
		preps.execute();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefeditor = prefs.edit();
		
		if (location != null) {
			prefeditor.putInt("latE6", (int) (location.getLatitude()*1E6));
			prefeditor.putInt("lngE6", (int) (location.getLongitude()*1E6));
			prefeditor.putInt("zoom", 7);
			prefeditor.commit();
		}
	}

	/**
	 * To be called after preparation async task finishes
	 */
	protected void load() {
		// setup action bar for tabs
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		// add map tab
		Tab mapTab = actionBar
				.newTab()
				.setText(MapFragment.TAG)
				.setTabListener(
						new TabListener<MapFragment>(this, MapFragment.TAG,
								MapFragment.class));
		actionBar.addTab(mapTab);

		// add tides tab
		Tab tideTab = actionBar
				.newTab()
				.setText(TideListFragment.TAG)
				.setTabListener(
						new TabListener<TideListFragment>(this,
								TideListFragment.TAG, TideListFragment.class));
		actionBar.addTab(tideTab);

		// add currents tab
		Tab currTab = actionBar
				.newTab()
				.setText(CurrentListFragment.TAG)
				.setTabListener(
						new TabListener<CurrentListFragment>(this,
								CurrentListFragment.TAG,
								CurrentListFragment.class));
		actionBar.addTab(currTab);

		Tab stationTab = actionBar
				.newTab()
				.setText(StationFragment.TAG)
				.setTabListener(
						new TabListener<StationFragment>(this,
								StationFragment.TAG, StationFragment.class));
		actionBar.addTab(stationTab);

		// initialize xtide data
		File[] tcdLst = Constants.GetMXTideDir().listFiles();
		// xtideJni = new XtideJni();

		/*
		 * We don't want to load harmonics twice
		 */
//		if (xtideJni.getStationIndex().isEmpty()) {
			for (File f : tcdLst) {
				if (f.isFile() && f.canRead()) {
					xtideJni.loadHarmonicsI(f.getAbsolutePath());
				}
			}
//		}

		String[] stations = xtideJni.getStationIndexI().split("\n");
		Location tempLoc = new Location("temp");
		for (String sta : stations) {
			final Station s = new Station(sta);
			if (s.getStationType().equals(Station.TYPE_TIDE))
				tideStations.add(s);
			else
				currentStations.add(s);
			if (location != null) {
				tempLoc.setLatitude(s.getLatE6() / 1E6);
				tempLoc.setLongitude(s.getLngE6() / 1E6);
				s.setDistance(location.distanceTo(tempLoc));
			}
		}
		
		if (location != null) {
			Collections.sort(tideStations, SorterFactory.getStationDistanceSorter());
		}
		setSelectedStation(tideStations.get(0));
	}

	public void goToTideTab() {
		getActionBar().setSelectedNavigationItem(1);
	}

	public void goToCurrentTab() {
		getActionBar().setSelectedNavigationItem(2);
	}

	public void goToStationTab() {
		getActionBar().setSelectedNavigationItem(3);
	}

	public void setSelectedStation(final Station station) {
		//final long epoch = System.currentTimeMillis() / 1000;
		//setStationDataTime(epoch, station);
		this.selectedStation = station;
	}
	
	public void setStationDataTime(final long epoch, final Station station) {
		station.setAbout(xtideJni.getStationAboutI(station.getName(), epoch));
		station.setRawData(xtideJni.getStationRawDataI(station.getName(), epoch));
		station.setData(xtideJni.getStationPlainDataI(station.getName(), epoch));
		station.setTime(xtideJni.getStationTimestampI(station.getName(), epoch));
		station.setPrecition(xtideJni.getStationPredictionI(station.getName(), epoch));
	}

	public BoundingBoxE6 getMapBox() {
		return mapBox;
	}

	public void setMapBox(final BoundingBoxE6 box) {
		mapBox = box;
	}

	public MyMapView getMapView() {
		return mMapView;
	}

	public void setMapView(final MyMapView mmv) {
		mMapView = mmv;
	}

	public Station getSelectedStation() {
		return selectedStation;
	}

	public Location getLocation() {
		return location;
	}

	public ArrayList<Station> getTideStationList() {
		return tideStations;
	}

	public ArrayList<Station> getCurrentStationList() {
		return currentStations;
	}
	
	public SharedPreferences.Editor getPrefEditor() {
		return prefeditor;
	}
	
	public SharedPreferences getPrefs() {
		return prefs;
	}

	public ArrayList<Station> getTideStationSubList() {
		return subListTides;
	}

	public ArrayList<Station> getCurrStationSubList() {
		return subListCurrents;
	}

	public ArrayList<Station> getStationSubList(final String stationType) {
		final ArrayList<Station> subList = stationType.equals(Station.TYPE_TIDE) ? subListTides
				: subListCurrents;
		if (useMapBox) {
			subList.clear();
			final GeoPoint gp = new GeoPoint(0,0);
			for (Station s: stationType.equals(Station.TYPE_TIDE) ? tideStations : currentStations) {
				gp.setCoordsE6(s.getLatE6(), s.getLngE6());
				if (mapBox.contains(gp))
					subList.add(s);
			}
		}
		return subList;
	}
	
	public void setUseMapBox(final boolean use) {
		useMapBox = use;
	}

	protected XtideJni getXtideJni() {
		return xtideJni;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//
//		// SearchView searchView = (SearchView)
//		// menu.findItem(R.id.menu_search).getActionView();
//		// TODO:
//		// http://developer.android.com/guide/topics/search/search-dialog.html
//
//		return true;
//	}

	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final MainActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		public TabListener(MainActivity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
			}
			ft.add(R.id.container, mFragment, mTag);
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.remove(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

}
