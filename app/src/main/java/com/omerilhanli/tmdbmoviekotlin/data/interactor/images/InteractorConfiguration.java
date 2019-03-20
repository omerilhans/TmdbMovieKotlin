package com.omerilhanli.tmdbmoviekotlin.data.interactor.images;

import com.omerilhanli.tmdbmoviekotlin.BuildConfig;
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi;
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor;
import com.omerilhanli.tmdbmoviekotlin.data.model.Configuration;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InteractorConfiguration implements Interactor<Configuration> {

    private TmdbApi tmdbApi;

    public InteractorConfiguration(TmdbApi tmdbApi){

        this.tmdbApi=tmdbApi;
    }

    @Override
    public Observable<Configuration> onRequest(Object value) {

        return tmdbApi.getConfiguration(BuildConfig.TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
