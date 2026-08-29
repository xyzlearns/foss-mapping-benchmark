package com.fossmapping.model;

import java.util.List;

public record RouteResult(
        double distance,
        double duration,
        List<List<Double>> geometry
) {
}