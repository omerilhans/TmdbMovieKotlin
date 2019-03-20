package com.omerilhanli.tmdbmoviekotlin.api

import com.omerilhanli.tmdbmoviekotlin.api.constants.ApiConstant
import com.omerilhanli.tmdbmoviekotlin.api.constants.ApiEndPoint
import com.omerilhanli.tmdbmoviekotlin.data.model.Configuration
import com.omerilhanli.tmdbmoviekotlin.data.model.Genres
import com.omerilhanli.tmdbmoviekotlin.data.model.Movie
import com.omerilhanli.tmdbmoviekotlin.data.model.Movies
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable


interface TmdbApi {

    @GET(ApiEndPoint.END_ALL)
     fun getMovies(
        @Query(ApiConstant.KEY_API) apiKey: String,
        @Query(ApiConstant.KEY_RELEASE_DATE) releaseDate: String,
        @Query(ApiConstant.KEY_SORT_BY) sortBy: String, @Query(ApiConstant.KEY_PAGE) page: Int
    ): Observable<Movies>

    @GET(ApiEndPoint.END_NOW_PLAYING)
     fun getMoviesNowPlaying(@Query(ApiConstant.KEY_API) apiKey: String, @Query(ApiConstant.KEY_PAGE) page: Int): Observable<Movies>

    @GET(ApiEndPoint.END_POPULAR)
     fun getMoviesPopular(@Query(ApiConstant.KEY_API) apiKey: String, @Query(ApiConstant.KEY_PAGE) page: Int): Observable<Movies>

    @GET(ApiEndPoint.END_TOP_RATED)
     fun getMoviesTopRated(@Query(ApiConstant.KEY_API) apiKey: String, @Query(ApiConstant.KEY_PAGE) page: Int): Observable<Movies>

    @GET(ApiEndPoint.END_UP_COMING)
     fun getMoviesUpcoming(@Query(ApiConstant.KEY_API) apiKey: String, @Query(ApiConstant.KEY_PAGE) page: Int): Observable<Movies>


    @Headers("Cache-Control: public, max-stale=2419200")
    @GET(ApiEndPoint.END_CONFIGURATION)
     fun getConfiguration(@Query(ApiConstant.KEY_API) apiKey: String): Observable<Configuration>

    @GET(ApiEndPoint.END_GENRES)
     fun getGenres(@Query(ApiConstant.KEY_API) apiKey: String): Observable<Genres>

    // ------------------------------------------------------------------------

    @GET(ApiEndPoint.END_DETAIL)
     fun getMovie(@Path(ApiConstant.KEY_MOVE_ID) movieId: Int, @Query(ApiConstant.KEY_API) apiKey: String): Observable<Movie>

    @GET(ApiEndPoint.END_SEARCH)
     fun getSearchResult(
        @Query(ApiConstant.KEY_API) apiKey: String,
        @Query(ApiConstant.KEY_QUERY) query: String
    ): Observable<Movies>

}