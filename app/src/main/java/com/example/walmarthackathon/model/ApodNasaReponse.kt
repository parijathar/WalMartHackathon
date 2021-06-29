package com.example.fiapp.model;

import com.fasterxml.jackson.annotation.JsonProperty

data class ApodNasaReponse(
    @JsonProperty("copyright")
    var copyright: String? = null
) {

    @JsonProperty("date")
    var date: String? = null

    @JsonProperty("explanation")
    var explanation: String? = null

    @JsonProperty("hdurl")
    var hdurl: String? = null

    @JsonProperty("media_type")
    var media_type: String? = null

    @JsonProperty("service_version")
    var service_version: String? = null

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("url")
    var url: String? = null
}
