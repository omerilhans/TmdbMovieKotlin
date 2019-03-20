package com.omerilhanli.tmdbmoviekotlin.data.interactor.genres;

import com.omerilhanli.tmdbmoviekotlin.BuildConfig;
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi;
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor;
import com.omerilhanli.tmdbmoviekotlin.data.model.Genres;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InteractorGenres implements Interactor<Genres> {

    private TmdbApi tmdbApi;

    public InteractorGenres(TmdbApi tmdbApi){

        this.tmdbApi=tmdbApi;
    }

    @Override
    public Observable<Genres> onRequest(Object value) {

        return tmdbApi.getGenres(BuildConfig.TMDB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
