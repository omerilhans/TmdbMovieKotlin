package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "adult",
    "backdrop_path",
    "belongs_to_collection",
    "budget",
    "genres",
    "homepage",
    "id",
    "imdb_id",
    "original_language",
    "original_title",
    "overview",
    "popularity",
    "poster_path",
    "production_companies",
    "production_countries",
    "release_date",
    "revenue",
    "runtime",
    "spoken_languages",
    "status",
    "tagline",
    "title",
    "video",
    "vote_average",
    "vote_count"
)
data class Movie (

    @JsonProperty("adult")
    var adult: Boolean = false,
    @JsonProperty("backdrop_path")
    var backdropPath: String? = null,
    @JsonProperty("belongs_to_collection")
    var belongsToCollection: BelongsToCollection? = null,
    @JsonProperty("budget")
    var budget: Int = 0,
    @JsonProperty("genre_ids")
    var genresId: List<Int>? = null,
    @JsonProperty("genres")
    var genres: List<Genres>? = null,
    @JsonProperty("homepage")
    var homepage: String? = null,
    @JsonProperty("id")
    var id: Int = 0,
    @JsonProperty("imdb_id")
    var imdbId: String? = null,
    @JsonProperty("original_language")
    var originalLanguage: String? = null,
    @JsonProperty("original_title")
    var originalTitle: String? = null,
    @JsonProperty("overview")
    var overview: String? = null,
    @JsonProperty("popularity")
    var popularity: Float = 0.toFloat(),
    @JsonProperty("poster_path")
    var posterPath: String? = null,
    @JsonProperty("production_companies")
    var productionCompanies: List<ProductionCompany>? = null,
    @JsonProperty("production_countries")
    var productionCountries: List<ProductionCountry>? = null,
    @JsonProperty("release_date")
    var releaseDate: String? = null,
    @JsonProperty("revenue")
    var revenue: Int = 0,
    @JsonProperty("runtime")
    var runtime: Int = 0,
    @JsonProperty("spoken_languages")
    var spokenLanguages: List<SpokenLanguage>? = null,
    @JsonProperty("status")
    var status: String? = null,
    @JsonProperty("tagline")
    var tagline: String? = null,
    @JsonProperty("title")
    var title: String? = null,
    @JsonProperty("video")
    var video: Boolean = false,
    @JsonProperty("vote_average")
    var voteAverage: Float = 0.toFloat(),
    @JsonProperty("vote_count")
    var voteCount: Int = 0
)
