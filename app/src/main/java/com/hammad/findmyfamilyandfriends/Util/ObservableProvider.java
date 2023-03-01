package com.hammad.findmyfamily.Util;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ObservableProvider {
    public static <T> Observable<T> getSmartObservable(ObservableOnSubscribe<T> observable) {
        return Observable.create(observable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> getObservable(ObservableOnSubscribe<T> observable) {
        return Observable.create(observable);
    }
}
