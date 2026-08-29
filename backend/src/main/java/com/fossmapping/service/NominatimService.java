package com.fossmapping.service;

import com.fossmapping.model.GeocodingResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class NominatimService {

    private final RestClient restClient;

    public NominatimService(){
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .build();
    }

    public GeocodingResult geocode(String address){
        JsonNode[] results = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                    .queryParam("q",address)
                    .queryParam("format","json")
                    .build())
                .header("User-Agent", "FOSS-Mapping-Benchmark")
                .retrieve()
                .body(JsonNode[].class);

        if (results == null || results.length == 0) {
            return null;
        }

        JsonNode result = results[0];

        return new GeocodingResult(result.get("lat").asDouble(),
                result.get("lon").asDouble()
                ,result.get("display_name").asText());

    }

    public String reverseGeocode(double latitude, double longitude) {

        JsonNode result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("format", "json")
                        .build())
                .header("User-Agent", "FOSS-Mapping-Benchmark")
                .retrieve()
                .body(JsonNode.class);

        return result.get("display_name").asText();
    }
}
