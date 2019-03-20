package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "base_url",
    "secure_base_url",
    "backdrop_sizes",
    "logo_sizes",
    "poster_sizes",
    "profile_sizes",
    "still_sizes"
)
data class Images(
    @JsonProperty("base_url") val baseUrl: String?=null,
    @JsonProperty("secure_base_url") val secureBaseUrl: String?=null,
    @JsonProperty("backdrop_sizes") val backdropSizes: List<String>?=null,
    @JsonProperty("logo_sizes") val logoSizes: List<String>? = null,
    @JsonProperty("poster_sizes") val posterSizes: List<String>? = null,
    @JsonProperty("profile_sizes") val profileSizes: List<String>? = null,
    @JsonProperty("still_sizes") val stillSizes: List<String>? = null
)