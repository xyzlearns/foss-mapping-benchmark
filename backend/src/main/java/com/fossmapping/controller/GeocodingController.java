package com.fossmapping.controller;

import com.fossmapping.model.GeocodingResult;
import com.fossmapping.service.NominatimService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeocodingController {
    private final NominatimService nominatimService;


    public GeocodingController(
            NominatimService nominatimService) {

        this.nominatimService = nominatimService;

    }
    @GetMapping("/api/geocode")
    public GeocodingResult geocode(@RequestParam String address){
        return nominatimService.geocode(address);
    }

    @GetMapping("/api/reverse-geocode")
    public String reverseGeocode(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        return nominatimService.reverseGeocode(latitude, longitude);
    }

}
