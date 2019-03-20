package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("page", "results", "total_results", "total_pages", "dates")
data class Movies(
    @JsonProperty("page")
    var page: Int = 0,
    @JsonProperty("results")
    var movies: List<Movie>? = null,
    @JsonProperty("total_results")
    var totalResults: Int = 0,
    @JsonProperty("total_pages")
    var totalPages: Int = 0,
    @JsonProperty("dates")
    var dates: Dates? = null
) : Serializable

