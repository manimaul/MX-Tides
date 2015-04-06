package com.mxmariner.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import com.mxmariner.signal.PredictionTimeSignal;
import com.mxmariner.signal.SignalDispatch;

public class TimePickerDialogFragment extends DialogFragment {
    public static final String TAG = TimePickerDialogFragment.class.getSimpleName();
    private static final String ARG_SIGNAL = "ARG_SIGNAL";

    private PredictionTimeSignal.Builder signalBuilder;

    public static TimePickerDialogFragment createFragment(String stationDateString) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle args = new Bundle();
        PredictionTimeSignal.Builder signalBuilder = PredictionTimeSignal.builder()
                .setDateWithStationDateString(stationDateString);
        args.putSerializable(ARG_SIGNAL, signalBuilder);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SIGNAL)) {
            signalBuilder = (PredictionTimeSignal.Builder) args.getSerializable(ARG_SIGNAL);
        } else {
            signalBuilder = PredictionTimeSignal.builder();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        PredictionTimeSignal timeSignal = signalBuilder.build();
        return new TimePickerDialog(getActivity(), new TimeChangedListener(), timeSignal.getHour(), timeSignal.getMinute(), false);
    }

    private class TimeChangedListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            signalBuilder.setHour(hourOfDay)
                    .setMinute(minute)
                    .setShouldRefreshUI(true);

            SignalDispatch.getInstance()
                    .publishStationPredictionTime(signalBuilder.build());
        }
    }

}
