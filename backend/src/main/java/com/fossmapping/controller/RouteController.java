package com.fossmapping.controller;

import com.fossmapping.model.GeocodingResult;
import com.fossmapping.model.RouteComparisonResponse;
import com.fossmapping.model.RouteRequest;
import com.fossmapping.model.RouteResult;
import com.fossmapping.service.NominatimService;
import com.fossmapping.service.OsrmService;
import com.fossmapping.service.ValhallaService;
import com.fossmapping.service.GoogleRoutesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    private final NominatimService nominatimService;
    private final OsrmService osrmService;
    private final ValhallaService valhallaService;
    private final GoogleRoutesService googleRoutesService;

    public RouteController(
            NominatimService nominatimService,
            OsrmService osrmService,
            ValhallaService valhallaService,
            GoogleRoutesService googleRoutesService) {

        this.nominatimService = nominatimService;
        this.osrmService = osrmService;
        this.valhallaService = valhallaService;
        this.googleRoutesService = googleRoutesService;
    }

    @PostMapping("/compare")
    public RouteComparisonResponse compare(
            @RequestBody RouteRequest request) {

        GeocodingResult start =
                nominatimService.geocode(
                        request.startAddress()
                );

        GeocodingResult end =
                nominatimService.geocode(
                        request.endAddress()
                );

        if (start == null || end == null) {
            throw new RuntimeException(
                    "Unable to geocode one or both addresses"
            );
        }

        System.out.println("start");

        RouteResult osrm =
                osrmService.getRoute(
                        start.longitude(),
                        start.latitude(),
                        end.longitude(),
                        end.latitude()
                );

        System.out.println("osrm");

        RouteResult valhalla =
                valhallaService.getRoute(
                        start.longitude(),
                        start.latitude(),
                        end.longitude(),
                        end.latitude()
                );

        System.out.println("valhalla");

        RouteResult google =
                googleRoutesService.getRoute(
                        start.longitude(),
                        start.latitude(),
                        end.longitude(),
                        end.latitude()
                );

        System.out.println("google");

        return new RouteComparisonResponse(
                osrm,
                valhalla,
                google
        );
    }
}