package com.mxmariner.tides;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class StationFragment extends Fragment {
	public static final String TAG = "Station";
	Station station;
	MainActivity mainActivity;
	Button dateBtn;
	Button timeBtn;
	
	long epoch;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stationlayout, container, false);
    }
	
	private void setup() {
		
		final int splitIndex = station.getTime().indexOf(" ");
		dateBtn.setText(station.getTime().substring(0, splitIndex));
		timeBtn.setText(station.getTime().substring(splitIndex, station.getTime().length()));
		
		((TextView) getView().findViewById(R.id.stationprediction)).setText(station.getPrecition());
		
		final int dkgrey = mainActivity.getResources().getColor(R.color.dkgrey);
		
		LinearLayout llp = (LinearLayout) getView().findViewById(R.id.plaindata);
		llp.removeAllViews();
		for (int i=1; i<station.getData().length; i++) {
			String s = station.getData()[i].trim();
			TextView tv = new TextView(mainActivity);
			if (s.contains(dateBtn.getText()))
				tv.setText(s.substring(dateBtn.getText().length(), s.length()).trim());
			else
				tv.setText(s);
			tv.setTextColor(Color.WHITE);
			if (i % 2 != 0)
				tv.setBackgroundColor(dkgrey);
			llp.addView(tv);
		}
		
		// init example series data
		GraphViewData[] data = new GraphViewData[station.getRawData().length];
		
		for (int i=0; i<station.getRawData().length; i++) {
			String[] d = station.getRawData()[i].split(" ");
			data[i] = new GraphViewData(Double.valueOf(d[0]), Double.valueOf(d[1]));
		}
		
		GraphViewSeries series = new GraphViewSeries(data);
		//GraphViewSeries series = new GraphViewSeries("", new GraphViewSeries.GraphViewStyle(Color.rgb(90, 250, 00), 3), data); 
		  
		GraphView graphView;
		if (station.getStationType().equals(Station.TYPE_TIDE)) {
			graphView = new BarGraphView(mainActivity, dateBtn.getText().toString());
		} else {
			graphView = new LineGraphView(mainActivity, dateBtn.getText().toString());
			GraphViewData[] dataZero = new GraphViewData[2];
			Double d = Double.valueOf(station.getRawData()[0].split(" ")[0]);
			dataZero[0] = new GraphViewData(Double.valueOf(d), 0);
			d = Double.valueOf(station.getRawData()[station.getRawData().length-1].split(" ")[0]);
			dataZero[1] = new GraphViewData(Double.valueOf(d), 0);
			GraphViewSeries seriesZero = new GraphViewSeries("", new GraphViewSeries.GraphViewStyle(Color.rgb(255, 255, 255), 3), dataZero);
			graphView.addSeries(seriesZero);
		}
		
		graphView.setHorizontalLabels(new String[] {"12am", "2am", "4am", "6am", "8am", "10am", "12pm", "2pm", "4pm", "6pm", "8pm", "10pm", "12am"});
		
		graphView.addSeries(series); // data 
		
		LinearLayout llg = (LinearLayout) getView().findViewById(R.id.graph);
		llg.removeAllViews();
		llg.addView(graphView);
	}
	
	@Override
	public void  onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mainActivity = (MainActivity) getActivity();
		station = mainActivity.getSelectedStation();
		((TextView) getView().findViewById(R.id.stationname)).setText(station.getName());
		epoch = System.currentTimeMillis() / 1000;
		mainActivity.setStationDataTime(epoch, station);
		
		((Button) getView().findViewById(R.id.btnfwd)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				epoch += 86400;
				mainActivity.setStationDataTime(epoch, station);
				setup();
			}
		});
		
		((Button) getView().findViewById(R.id.btnbck)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				epoch -= 86400;
				mainActivity.setStationDataTime(epoch, station);
				setup();
			}
		});
		
		dateBtn = ((Button) getView().findViewById(R.id.btndate));
		dateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Dialog dlg = new Dialog(mainActivity) {
					@Override
					public void onStart() {
						final DatePicker dpick = (DatePicker) findViewById(R.id.datepick);
						String[] ymd = dateBtn.getText().toString().trim().split("-");
						dpick.updateDate(Integer.valueOf(ymd[0]), Integer.valueOf(ymd[1])-1, Integer.valueOf(ymd[2]));
						final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
						calendar.set(dpick.getYear(), dpick.getMonth(), dpick.getDayOfMonth());
						final long millis = calendar.getTimeInMillis();
						final Button btnOk = (Button) findViewById(R.id.btndateok);
						btnOk.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								//XXX
								calendar.set(dpick.getYear(), dpick.getMonth(), dpick.getDayOfMonth());
								long change = calendar.getTimeInMillis() - millis;
								epoch += (change / 1000);
								mainActivity.setStationDataTime(epoch, station);
								setup();
								dismiss();
							}
						});
					}
					
				};
				dlg.setTitle("Select Date");
				dlg.setContentView(R.layout.dateselect);
				dlg.show();
				
			}
		});
		timeBtn = ((Button) getView().findViewById(R.id.btntime));
		timeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog dlg = new Dialog(mainActivity) {
					@Override
					public void onStart() {
						final TimePicker tpick = (TimePicker) findViewById(R.id.timepick);
						String[] taz = timeBtn.getText().toString().trim().split(" ");
						String[] hm = taz[0].split(":");
						int hour = Integer.valueOf(hm[0]);
						if (taz[1].equals("PM"))
							hour += 12;
						tpick.setCurrentHour(hour);
						tpick.setCurrentMinute(Integer.valueOf(hm[1]));
						
						final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
						calendar.set(Calendar.HOUR, tpick.getCurrentHour());
						calendar.set(Calendar.MINUTE, tpick.getCurrentMinute());
						final long millis = calendar.getTimeInMillis();
						final Button btnOk = (Button) findViewById(R.id.btntimeok);
						btnOk.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								//XXX
								calendar.set(Calendar.HOUR, tpick.getCurrentHour());
								calendar.set(Calendar.MINUTE, tpick.getCurrentMinute());
								long change = calendar.getTimeInMillis() - millis;
								epoch += (change / 1000);
								mainActivity.setStationDataTime(epoch, station);
								setup();
								dismiss();
							}
						});
					}
					
				};
				dlg.setTitle("Select Time");
				dlg.setContentView(R.layout.timeselect);
				dlg.show();
				
			}
		});
		
		setup();
		
	}


}
