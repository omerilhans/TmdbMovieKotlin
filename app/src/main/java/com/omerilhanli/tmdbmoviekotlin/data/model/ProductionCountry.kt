package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("iso_3166_1", "name")
data class ProductionCountry(
    @JsonProperty("iso_3166_1") var iso31661: String? = null,
    @JsonProperty("name") var name: String? = null
)
