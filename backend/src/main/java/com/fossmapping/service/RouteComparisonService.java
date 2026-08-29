package com.fossmapping.service;

import com.fossmapping.model.RouteComparison;
import com.fossmapping.model.RouteResult;
import org.springframework.stereotype.Service;

@Service
public class RouteComparisonService {

    private final OsrmService osrmService;
    private final ValhallaService valhallaService;

    public RouteComparisonService(
            OsrmService osrmService,
            ValhallaService valhallaService) {

        this.osrmService = osrmService;
        this.valhallaService = valhallaService;
    }

    public RouteComparison compare(
            double startLongitude,
            double startLatitude,
            double endLongitude,
            double endLatitude) {

        RouteResult osrm = osrmService.getRoute(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );

        RouteResult valhalla = valhallaService.getRoute(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );

        double distanceDifference =
                Math.abs(osrm.distance() - valhalla.distance());

        double durationDifference =
                Math.abs(osrm.duration() - valhalla.duration());

        return new RouteComparison(
                osrm,
                valhalla,
                distanceDifference,
                durationDifference,
                distanceDifference / osrm.distance() * 100,
                durationDifference / osrm.duration() * 100
        );
    }
}