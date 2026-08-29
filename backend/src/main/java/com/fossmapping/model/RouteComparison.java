package com.fossmapping.model;

public record RouteComparison(
        RouteResult osrm,
        RouteResult valhalla,
        double distanceDifferenceKm,
        double durationDifferenceSeconds,
        double distanceDifferencePercent,
        double durationDifferencePercent
) {
}