package com.mxmariner.signal;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import rx.Observable;

public class PredictionTimeSignal implements Serializable {
    public static final String TAG = PredictionTimeSignal.class.getSimpleName();

    private final long epoch;
    private final boolean shouldRefreshUI;
    private final String tag;
    private Calendar calendar = null;

    /**
     *
     * @param epochMillisUTC time to signal
     * @param shouldRefreshUI inform subscribers they should refreshUI
     * @param tag optional tag
     */
    public PredictionTimeSignal(long epochMillisUTC, boolean shouldRefreshUI, @Nullable String tag) {
        this.epoch = epochMillisUTC;
        this.shouldRefreshUI = shouldRefreshUI;
        this.tag = tag;
    }

    /**
     *
     * @return a signal builder with time set to current time now
     */
    public static Builder builder() {
        return new Builder();
    }

    public long getEpochSeconds() {
        return epoch / 1000l;
    }

    public long getEpochMillisUTC() {
        return epoch;
    }

    public boolean shouldRefreshUI() {
        return shouldRefreshUI;
    }

    public int getHour() {
        return getCalendar().get(Calendar.HOUR);
    }

    public int getMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }

    private Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(epoch);
        }
        return calendar;
    }

    @Nullable
    public String getTag() {
        return tag;
    }

    public int getYear() {
        return getCalendar().get(Calendar.YEAR);
    }

    public int getMonthOfYear() {
        return getCalendar().get(Calendar.MONTH);
    }

    public int getDayOfMonth() {
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public String toString() {
        return getCalendar().toString();
    }

    public static class Builder implements Serializable {
        private final Calendar calendar = Calendar.getInstance();
        private boolean shouldRefreshUI = false;
        private String tag;

        public Builder setDateWithStationDateString(String dateString) {
            //todo:
            return this;
        }

        public Builder setYear(int year) {
            calendar.set(Calendar.YEAR, year);
            return this;
        }

        public Builder setEpochMillisUTC(long millis) {
            calendar.setTimeInMillis(millis);
            return this;
        }

        public Builder setMonthOfYear(int monthOfYear) {
            calendar.set(Calendar.MONTH, monthOfYear);
            return this;
        }

        public Builder setDayOfMonth(int dayOfMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            return this;
        }

        public Builder setHour(int hour) {
            calendar.set(Calendar.HOUR, hour);
            return this;
        }

        public Builder setMinute(int minute) {
            calendar.set(Calendar.MINUTE, minute);
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setShouldRefreshUI(boolean shouldRefreshUI) {
            this.shouldRefreshUI = shouldRefreshUI;
            return this;
        }

        public PredictionTimeSignal build() {
            return new PredictionTimeSignal(calendar.getTimeInMillis(), shouldRefreshUI, tag);
        }

        public Observable<PredictionTimeSignal> buildObservable() {
            return Observable.just(build());
        }
    }
}
