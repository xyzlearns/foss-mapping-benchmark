package com.fossmapping.service;

import com.fossmapping.model.RouteResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleRoutesService {

    private final RestClient restClient;

    public GoogleRoutesService(
            @Value("${google.maps.api.key}") String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl("https://routes.googleapis.com")
                .defaultHeader("X-Goog-Api-Key", apiKey)
                .defaultHeader(
                        "X-Goog-FieldMask",
                        "routes.distanceMeters,"
                                + "routes.duration,"
                                + "routes.polyline.geoJsonLinestring"
                )
                .build();
    }

    public RouteResult getRoute(
            double startLongitude,
            double startLatitude,
            double endLongitude,
            double endLatitude) {

        String body = """
                {
                  "origin": {
                    "location": {
                      "latLng": {
                        "latitude": %f,
                        "longitude": %f
                      }
                    }
                  },
                  "destination": {
                    "location": {
                      "latLng": {
                        "latitude": %f,
                        "longitude": %f
                      }
                    }
                  },
                  "travelMode": "DRIVE",
                  "polylineQuality": "HIGH_QUALITY",
                  "polylineEncoding": "GEO_JSON_LINESTRING"
                }
                """.formatted(
                startLatitude,
                startLongitude,
                endLatitude,
                endLongitude
        );

        JsonNode result = restClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        JsonNode route =
                result.get("routes").get(0);

        double distance =
                route.get("distanceMeters").asDouble()
                        / 1000.0;

        String durationText =
                route.get("duration").asText();

        double duration =
                Double.parseDouble(
                        durationText.replace("s", "")
                );

        JsonNode coordinates =
                route.get("polyline")
                        .get("geoJsonLinestring")
                        .get("coordinates");

        List<List<Double>> geometry =
                new ArrayList<>();

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