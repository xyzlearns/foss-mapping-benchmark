package com.fossmapping.model;

public record RouteComparison(
        RouteResult osrm,
        RouteResult valhalla,
        RouteResult google,
        double distanceDifferenceKm,
        double durationDifferenceSeconds,
        double distanceDifferencePercent,
        double durationDifferencePercent
) {
}
