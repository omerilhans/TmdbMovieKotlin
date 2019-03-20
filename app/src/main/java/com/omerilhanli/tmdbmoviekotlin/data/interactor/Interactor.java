package com.omerilhanli.tmdbmoviekotlin.data.interactor;

import rx.Observable;

public interface Interactor<T> {

    Observable<T> onRequest(Object value);
}
