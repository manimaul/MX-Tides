package com.mxmariner.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import com.mxmariner.signal.PredictionTimeSignal;
import com.mxmariner.signal.SignalDispatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePickerDialogFragment extends DialogFragment {
    public static final String TAG = TimePickerDialogFragment.class.getSimpleName();
    private static final String ARG_DATE_STRING = "ARG_DATE_STRING";
    private long actualEpoch;
    private long diffEpoch;
    private Calendar calendar = Calendar.getInstance();

    public static TimePickerDialogFragment createFragment(String stationDateString) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE_STRING, stationDateString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_DATE_STRING)) {
            String dateString = args.getString(ARG_DATE_STRING);

            if (dateString.length() >= 16) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa z");
                try {
                    Date deviceLocalTime = sdf.parse(dateString);
                    Log.d(TAG, "deviceLocalTime: " + deviceLocalTime.toString());
                    Log.d(TAG, "stationLocalTime: " + dateString);
                    actualEpoch = deviceLocalTime.getTime();

                    //2014-04-05 02:30 PM CDT
                    //yyyy-MM-dd hh:mm aa zzz
                    int hour = Integer.parseInt(dateString.substring(11, 13));
                    int minute = Integer.parseInt(dateString.substring(14, 16));
                    int amPm = dateString.substring(17, 19).equalsIgnoreCase("AM") ?
                            Calendar.AM : Calendar.PM;

                    calendar.set(Calendar.HOUR, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.AM_PM, amPm);
                    diffEpoch = calendar.getTimeInMillis();
                } catch (ParseException | NumberFormatException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), new TimeChangedListener(), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
    }

    private class TimeChangedListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); /* 24 hour */
            calendar.set(Calendar.MINUTE, minute);

            long delta = calendar.getTimeInMillis() - diffEpoch;
            Log.d(TAG, "delta in minutes: " + delta / (1000 * 60));
            actualEpoch += delta;

            SignalDispatch.getInstance()
                    .publishStationPredictionTime(PredictionTimeSignal.createSignalWithEpochMillis(actualEpoch));
        }
    }

}
