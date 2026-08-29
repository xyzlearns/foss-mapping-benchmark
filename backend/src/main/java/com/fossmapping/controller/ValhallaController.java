package com.fossmapping.controller;

import com.fossmapping.model.RouteResult;
import com.fossmapping.service.ValhallaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValhallaController {

    private final ValhallaService valhallaService;

    public ValhallaController(ValhallaService valhallaService) {
        this.valhallaService = valhallaService;
    }

    @GetMapping("/api/route/valhalla")
    public RouteResult getRoute(
            @RequestParam double startLongitude,
            @RequestParam double startLatitude,
            @RequestParam double endLongitude,
            @RequestParam double endLatitude) {

        return valhallaService.getRoute(
                startLongitude,
                startLatitude,
                endLongitude,
                endLatitude
        );
    }
}