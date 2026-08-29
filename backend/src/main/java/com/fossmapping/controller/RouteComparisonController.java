package com.fossmapping.controller;

import com.fossmapping.model.RouteComparison;
import com.fossmapping.service.RouteComparisonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteComparisonController {

    private final RouteComparisonService comparisonService;

    public RouteComparisonController(RouteComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping("/api/route/compare")
    public RouteComparison compare(
            @RequestParam double startLongitude,
            @RequestParam double startLatitude,
            @RequestParam double endLongitude,
            @RequestParam double endLatitude) {

        return comparisonService.compare(
                startLongitude, startLatitude,
                endLongitude, endLatitude
        );
    }
}