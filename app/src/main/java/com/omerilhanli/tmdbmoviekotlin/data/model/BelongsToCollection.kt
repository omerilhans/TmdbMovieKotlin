package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("id", "name", "poster_path", "backdrop_path")
data class BelongsToCollection(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("poster_path") val posterPath: String,
    @JsonProperty("backdrop_path") val backdropPath: String
)