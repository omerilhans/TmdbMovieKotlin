package com.omerilhanli.tmdbmoviekotlin.data.interactor.movies;

import com.omerilhanli.tmdbmoviekotlin.BuildConfig;
import com.omerilhanli.tmdbmoviekotlin.api.TmdbApi;
import com.omerilhanli.tmdbmoviekotlin.asistant.tools.DateFormatter;
import com.omerilhanli.tmdbmoviekotlin.data.interactor.Interactor;
import com.omerilhanli.tmdbmoviekotlin.data.model.Movies;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InteractorAll implements Interactor<Movies> {

    private TmdbApi tmdbApi;

    public InteractorAll(TmdbApi tmdbApi) {
        this.tmdbApi = tmdbApi;
    }

    @Override
    public Observable<Movies> onRequest(Object value) {

        String releaseDate = DateFormatter.INSTANCE.getReleaseDate();

        String sortBy = "primary_release_date.asc";

        return tmdbApi
                .getMovies(BuildConfig.TMDB_API_KEY, releaseDate, sortBy, (Integer) value)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
