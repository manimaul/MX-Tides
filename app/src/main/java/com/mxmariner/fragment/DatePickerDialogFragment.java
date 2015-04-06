package com.mxmariner.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.mxmariner.signal.PredictionTimeSignal;
import com.mxmariner.signal.SignalDispatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment {
    public static final String TAG = TimePickerDialogFragment.class.getSimpleName();
    private static final String ARG_DATE_STRING = "ARG_DATE_STRING";
    private long actualEpoch;
    private long diffEpoch;
    private Calendar calendar = Calendar.getInstance();

    public static DatePickerDialogFragment createFragment(String dateString) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE_STRING, dateString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_DATE_STRING)) {
            String dateString = args.getString(ARG_DATE_STRING);

            if (dateString.length() >= 10) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa z");
                try {
                    actualEpoch = sdf.parse(dateString).getTime();
                    //2014-04-05 02:30 PM CDT
                    //yyyy-MM-dd hh:mm aa zzz
                    int year = Integer.parseInt(dateString.substring(0, 4));
                    int month = Integer.parseInt(dateString.substring(5, 7));
                    int day = Integer.parseInt(dateString.substring(8, 10));
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    diffEpoch = calendar.getTimeInMillis();
                } catch (ParseException e) {
                    Log.e(TAG, "", e);
                }
            }


        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), new DateChangedListener(), calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private class DateChangedListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker,  int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            actualEpoch += diffEpoch - calendar.getTimeInMillis();

            SignalDispatch.getInstance()
                    .publishStationPredictionTime(PredictionTimeSignal.builder()
                            .setEpochMillisUTC(actualEpoch)
                            .setShouldRefreshUI(true)
                            .build());
        }
    }

}
