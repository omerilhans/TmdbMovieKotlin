package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "id", "logo_path", "origin_country")
data class ProductionCompany(
    @JsonProperty("name") var name: String? = null,
    @JsonProperty("id") var id: Int = 0,
    @JsonProperty("logo_path") var logoPath: String? = null,
    @JsonProperty("origin_country") var originCountry: String? = null
)
