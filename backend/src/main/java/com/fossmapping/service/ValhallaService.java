package com.fossmapping.service;

import com.fossmapping.model.RouteResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValhallaService {

    private final RestClient restClient;

    public ValhallaService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://valhalla1.openstreetmap.de")
                .build();
    }

    public RouteResult getRoute(
            double startLongitude,
            double startLatitude,
            double endLongitude,
            double endLatitude) {

        String body = """
                {
                  "locations": [
                    {
                      "lat": %f,
                      "lon": %f
                    },
                    {
                      "lat": %f,
                      "lon": %f
                    }
                  ],
                  "costing": "auto",
                  "units": "kilometers"
                }
                """.formatted(
                startLatitude,
                startLongitude,
                endLatitude,
                endLongitude
        );

        JsonNode result = restClient.post()
                .uri("/route")
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        JsonNode trip = result.get("trip");

        JsonNode summary =
                trip.get("summary");

        double distance =
                summary.get("length").asDouble();

        double duration =
                summary.get("time").asDouble();

        /*
         * Valhalla returns the route shape
         * as an encoded polyline.
         */

        String encodedShape =
                trip.get("legs")
                        .get(0)
                        .get("shape")
                        .asText();

        List<List<Double>> geometry =
                decodePolyline6(encodedShape);

        return new RouteResult(
                distance,
                duration,
                geometry
        );
    }


    private List<List<Double>> decodePolyline6(
            String encoded) {

        List<List<Double>> coordinates =
                new ArrayList<>();

        int index = 0;

        long latitude = 0;
        long longitude = 0;

        while (index < encoded.length()) {

            /*
             * Decode latitude
             */

            long result = 0;
            int shift = 0;

            int b;

            do {

                b = encoded.charAt(index++) - 63;

                result |=
                        (long) (b & 0x1f) << shift;

                shift += 5;

            } while (b >= 0x20);

            long deltaLatitude =
                    ((result & 1) != 0)
                            ? ~(result >> 1)
                            : (result >> 1);

            latitude += deltaLatitude;


            /*
             * Decode longitude
             */

            result = 0;
            shift = 0;

            do {

                b = encoded.charAt(index++) - 63;

                result |=
                        (long) (b & 0x1f) << shift;

                shift += 5;

            } while (b >= 0x20);

            long deltaLongitude =
                    ((result & 1) != 0)
                            ? ~(result >> 1)
                            : (result >> 1);

            longitude += deltaLongitude;


            /*
             * Valhalla polyline6 uses 6 decimal places.
             *
             * GeoJSON/MapLibre wants:
             *
             * [longitude, latitude]
             */

            double decodedLatitude =
                    latitude / 1_000_000.0;

            double decodedLongitude =
                    longitude / 1_000_000.0;


            List<Double> point =
                    new ArrayList<>();

            point.add(decodedLongitude);
            point.add(decodedLatitude);

            coordinates.add(point);
        }

        return coordinates;
    }
}