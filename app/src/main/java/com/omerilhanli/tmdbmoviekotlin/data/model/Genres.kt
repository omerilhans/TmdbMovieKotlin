package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("genres", "id", "name")
data class Genres(
    @JsonProperty("genres") val genres: List<Genre>?=null,
    @JsonProperty("id") val id: Int?=null,
    @JsonProperty("name") val name: String?=null
)