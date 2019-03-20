package com.omerilhanli.tmdbmoviekotlin.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("images", "change_keys")
class Configuration(
    @JsonProperty("images") var images: Images? = null,
    @JsonProperty("change_keys") var changeKeys: List<String>? = null
)
