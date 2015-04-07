package com.mxmariner.signal;

public class PredictionTimeSignal {
    public static final String TAG = PredictionTimeSignal.class.getSimpleName();

    private final long epoch;

    public static PredictionTimeSignal createSignalTimeNow() {
        return new PredictionTimeSignal(System.currentTimeMillis());
    }

    public static PredictionTimeSignal createSignalWithEpochMillis(long epochMillis) {
        return new PredictionTimeSignal(epochMillis);
    }

    private PredictionTimeSignal(long epochMillisUTC) {
        this.epoch = epochMillisUTC;
    }

    public long getEpochSeconds() {
        return epoch / 1000l;
    }

    public long getEpochMillis() {
        return epoch;
    }

}
