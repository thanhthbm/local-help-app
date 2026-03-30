package com.localhelp.app.model.constant

object ApiConstants {
    // server
    const val BASE_URL = "http://10.0.2.2:3636"

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
    const val PUBLIC_KEY = "public_key"
    const val PRIVATE_KEY = ""
}