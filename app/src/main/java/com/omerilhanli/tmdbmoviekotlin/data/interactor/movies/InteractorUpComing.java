package com.omerilhanli.tmdbmoviekotlin.data.interactor.movies;

import com.omerilhanli.tmdbmoviekotlin.BuildConfig;
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi;
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor;
import com.omerilhanli.tmdbmoviekotlin.data.model.Movies;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InteractorUpComing implements Interactor<Movies> {

    private TmdbApi tmdbApi;

    public InteractorUpComing(TmdbApi tmdbApi) {

        this.tmdbApi = tmdbApi;
    }

    @Override
    public Observable<Movies> onRequest(Object value) {

        return tmdbApi.getMoviesUpcoming(BuildConfig.TMDB_API_KEY, (Integer) value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
