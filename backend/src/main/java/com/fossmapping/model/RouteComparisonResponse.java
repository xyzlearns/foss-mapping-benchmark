package com.fossmapping.model;

public record RouteComparisonResponse(
        RouteResult osrm,
        RouteResult valhalla,
        RouteResult google
) {
}