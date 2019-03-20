package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("iso_639_1", "name")
data class SpokenLanguage(
    @JsonProperty("iso_639_1") var iso6391: String? = null,
    @JsonProperty("name") var name: String? = null
)