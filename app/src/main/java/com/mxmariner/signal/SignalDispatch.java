package com.mxmariner.signal;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class SignalDispatch {

    private static SignalDispatch ourInstance = new SignalDispatch();

    private PublishSubject<Observable<DrawerMenuSignal>> drawerEventSubject = PublishSubject.create();
    private PublishSubject<Observable<Long>> stationIdEventSubject = PublishSubject.create();
    private BehaviorSubject<Observable<PredictionTimeSignal>> stationPredictionTimeSubject = BehaviorSubject.create();

    public static SignalDispatch getInstance() {
        return ourInstance;
    }

    private SignalDispatch() {
    }

    public Observable<DrawerMenuSignal> getDrawerEventObservable() {
        return Observable.switchOnNext(drawerEventSubject);
    }

    public void publishDrawerMenuSignal(DrawerMenuSignal signal) {
        drawerEventSubject.onNext(Observable.just(signal));
    }

    public Observable<Long> getStationIdEventObservable() {
        return Observable.switchOnNext(stationIdEventSubject);
    }

    public Observable<PredictionTimeSignal> getStationPredictionTimeObservable() {
        return Observable.switchOnNext(stationPredictionTimeSubject).retry();
    }

    public void publishStationIdSignal(Long id) {
        stationIdEventSubject.onNext(Observable.just(id));
    }

    public void publishStationPredictionTime(PredictionTimeSignal epoch) {
        stationPredictionTimeSubject.onNext(Observable.just(epoch));
    }

}
