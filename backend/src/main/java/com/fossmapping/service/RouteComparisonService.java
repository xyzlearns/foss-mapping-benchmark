package com.fossmapping.service;

import com.fossmapping.model.RouteComparison;
import com.fossmapping.model.RouteResult;
import org.springframework.stereotype.Service;

@Service
public class RouteComparisonService {

    private final OsrmService osrmService;
    private final ValhallaService valhallaService;
    private final GoogleRoutesService googleRoutesService;

    public RouteComparisonService(
            OsrmService osrmService,
            ValhallaService valhallaService,
            GoogleRoutesService googleRoutesService) {

        this.osrmService = osrmService;
        this.valhallaService = valhallaService;
        this.googleRoutesService = googleRoutesService;
    }

    public RouteComparison compare(
            double startLongitude,
            double startLatitude,
            double endLongitude,
            double endLatitude) {

        System.out.println("Calling OSRM...");

        RouteResult osrm = osrmService.getRoute(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );

        System.out.println("OSRM successful");

        System.out.println("Calling Valhalla...");

        RouteResult valhalla = valhallaService.getRoute(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );

        System.out.println("Valhalla successful");

        System.out.println("Calling Google...");

        RouteResult google = googleRoutesService.getRoute(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );

        System.out.println("Google successful");

        double distanceDifference =
                Math.abs(osrm.distance() - valhalla.distance());

        double durationDifference =
                Math.abs(osrm.duration() - valhalla.duration());

        return new RouteComparison(
                osrm,
                valhalla,
                google,
                distanceDifference,
                durationDifference,
                percentageDifference(distanceDifference, osrm.distance()),
                percentageDifference(durationDifference, osrm.duration())
        );
    }

    private double percentageDifference(double difference, double baseline) {
        return baseline == 0 ? 0 : difference / baseline * 100;
    }
}
