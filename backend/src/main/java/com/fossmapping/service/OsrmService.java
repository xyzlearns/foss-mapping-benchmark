package com.fossmapping.service;

import com.fossmapping.model.RouteResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class OsrmService {

    private final RestClient restClient;

    public OsrmService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://router.project-osrm.org")
                .build();
    }

    public RouteResult getRoute(
            double startLongitude,
            double startLatitude,
            double endLongitude,
            double endLatitude) {

        JsonNode result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route/v1/driving/"
                                + startLongitude + "," + startLatitude
                                + ";"
                                + endLongitude + "," + endLatitude)
                        .queryParam("overview", "full")
                        .queryParam("geometries", "geojson")
                        .build())
                .header("Accept-Encoding", "identity")
                .retrieve()
                .body(JsonNode.class);

        JsonNode route = result
                .get("routes")
                .get(0);

        double distance =
                route.get("distance").asDouble() / 1000.0;

        double duration =
                route.get("duration").asDouble();

        JsonNode coordinates =
                route.get("geometry").get("coordinates");

        List<List<Double>> geometry = new ArrayList<>();

        for (JsonNode coordinate : coordinates) {
            List<Double> point = new ArrayList<>();

            point.add(coordinate.get(0).asDouble());
            point.add(coordinate.get(1).asDouble());

            geometry.add(point);
        }

        return new RouteResult(
                distance,
                duration,
                geometry
        );
    }
}