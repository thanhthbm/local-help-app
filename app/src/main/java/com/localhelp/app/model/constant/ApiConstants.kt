package com.localhelp.app.model.constant

import com.localhelp.app.BuildConfig

object ApiConstants {
    // server
    const val BASE_URL = "http://192.168.1.9:6363"

    // Track asia map
    const val BASE_URL_MAP_VN = "https://maps.track-asia.com/"
    const val BASE_URL_MAP_SG = "https://sg-maps.track-asia.com/"
    const val BASE_URL_MAP_TH = "https://th-maps.track-asia.com/"
    const val BASE_URL_MAP_TW = "https://tw-maps.track-asia.com/"
    const val BASE_URL_MAP_MY = "https://my-maps.track-asia.com/"

    // API Endpoints
    const val GEOCODING_ENDPOINT = "api/v1/geocode"
    const val REVERSE_GEOCODING_ENDPOINT = "api/v1/reverse"
    const val AUTOCOMPLETE_ENDPOINT = "api/v1/autocomplete"
    const val DIRECTIONS_ENDPOINT = "route/v1/car"
    const val ELEVATION_ENDPOINT = "api/v1/elevation"

    // API Keys
    const val TRACK_ASIA_KEY = BuildConfig.TRACK_ASIA_API_KEY
}