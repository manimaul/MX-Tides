package com.mxmariner.event;

import rx.Observable;
import rx.subjects.PublishSubject;

public class Signals {

    private static Signals ourInstance = new Signals();

    private PublishSubject<Observable<DrawerMenuEvent>> drawerEventSubject = PublishSubject.create();
    private PublishSubject<Observable<Long>> stationIdEventSubject = PublishSubject.create();

    public static Signals getInstance() {
        return ourInstance;
    }

    private Signals() {
    }

    public Observable<DrawerMenuEvent> getDrawerEventObservable() {
        return Observable.switchOnNext(drawerEventSubject);
    }

    public void publishDrawerMenuEvent(DrawerMenuEvent event) {
        drawerEventSubject.onNext(Observable.just(event));
    }

    public Observable<Long> getStationIdEventObservable() {
        return Observable.switchOnNext(stationIdEventSubject);
    }

    public void publishStationIdEvent(Long id) {
        stationIdEventSubject.onNext(Observable.just(id));
    }

}
