package com.dedisive.foss.model

data class RouteRequest(
    val startAddress: String,
    val endAddress: String
)

data class RouteResult(
    val distance: Double,
    val duration: Double,
    val geometry: List<List<Double>>
)

data class RouteComparisonResponse(
    val osrm: RouteResult,
    val valhalla: RouteResult,
    val google: RouteResult
)