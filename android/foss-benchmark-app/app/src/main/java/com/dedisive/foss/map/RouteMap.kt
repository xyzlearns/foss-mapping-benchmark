package com.dedisive.foss.map

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dedisive.foss.model.RouteComparisonResponse
import com.dedisive.foss.model.RouteResult
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource

private data class StyledRoute(
    val id: String,
    val route: RouteResult,
    val color: Int
)

@Composable
fun RouteMap(
    comparison: RouteComparisonResponse?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
        }
    }

    var map by remember {
        mutableStateOf<MapLibreMap?>(null)
    }

    DisposableEffect(mapView) {

        mapView.onStart()

        onDispose {
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {

            mapView.apply {

                getMapAsync { loadedMap ->

                    loadedMap.setStyle(
                        Style.Builder()
                            .fromUri(
                                "https://tiles.openfreemap.org/styles/liberty"
                            )
                    ) {

                        map = loadedMap

                        comparison?.let {
                            drawComparison(
                                loadedMap,
                                it
                            )
                        }
                    }
                }
            }
        },

        update = {

            map?.let { loadedMap ->

                comparison?.let { result ->

                    drawComparison(
                        loadedMap,
                        result
                    )
                }
            }
        },

        modifier = modifier
    )
}

private fun drawComparison(
    map: MapLibreMap,
    comparison: RouteComparisonResponse
) {

    val routes = listOf(

        StyledRoute(
            id = "osrm",
            route = comparison.osrm,
            color = Color.rgb(
                33,
                150,
                243
            )
        ),

        StyledRoute(
            id = "valhalla",
            route = comparison.valhalla,
            color = Color.rgb(
                76,
                175,
                80
            )
        ),

        StyledRoute(
            id = "google",
            route = comparison.google,
            color = Color.rgb(
                244,
                67,
                54
            )
        )
    )

    val style = map.style ?: return

    /*
     * Draw routes
     */

    routes.forEach { route ->

        val source =
            style.getSource(route.id)
                    as? GeoJsonSource

        if (source == null) {

            style.addSource(
                GeoJsonSource(
                    route.id,
                    route.route.toGeoJson()
                )
            )

            style.addLayer(
                LineLayer(
                    "${route.id}-layer",
                    route.id
                ).withProperties(
                    lineColor(route.color),
                    lineWidth(5f)
                )
            )

        } else {

            source.setGeoJson(
                route.route.toGeoJson()
            )
        }
    }

    /*
     * Add start/end markers
     */

    val firstRoute =
        routes.firstOrNull()?.route

    val start =
        firstRoute
            ?.geometry
            ?.firstOrNull()

    val end =
        firstRoute
            ?.geometry
            ?.lastOrNull()

    if (start != null && end != null) {

        addEndpointLayer(
            style,
            start,
            end
        )
    }

    /*
     * Fit camera around all routes
     */

    val points =
        routes
            .flatMap {
                it.route.geometry
            }
            .mapNotNull {
                it.toLatLng()
            }

    if (points.isNotEmpty()) {

        val bounds =
            LatLngBounds.Builder()
                .includes(points)
                .build()

        map.animateCamera(
            CameraUpdateFactory
                .newLatLngBounds(
                    bounds,
                    80
                )
        )
    }
}

private fun addEndpointLayer(
    style: Style,
    start: List<Double>,
    end: List<Double>
) {

    val json =
        """
        {
          "type": "FeatureCollection",
          "features": [
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [
                  ${start[0]},
                  ${start[1]}
                ]
              }
            },
            {
              "type": "Feature",
              "geometry": {
                "type": "Point",
                "coordinates": [
                  ${end[0]},
                  ${end[1]}
                ]
              }
            }
          ]
        }
        """.trimIndent()

    val source =
        style.getSource("endpoints")
                as? GeoJsonSource

    if (source == null) {

        style.addSource(
            GeoJsonSource(
                "endpoints",
                json
            )
        )

        style.addLayer(
            CircleLayer(
                "endpoints-layer",
                "endpoints"
            ).withProperties(
                circleColor(Color.DKGRAY),
                circleRadius(7f)
            )
        )

    } else {

        source.setGeoJson(json)
    }
}

/*
 * Convert route geometry to GeoJSON LineString
 */

private fun RouteResult.toGeoJson(): String {

    val coordinates =
        geometry.joinToString(
            separator = ",",
            prefix = "[",
            postfix = "]"
        ) {

            "[${it[0]},${it[1]}]"
        }

    return """
        {
          "type": "Feature",
          "geometry": {
            "type": "LineString",
            "coordinates": $coordinates
          }
        }
    """.trimIndent()
}

/*
 * Convert [longitude, latitude]
 * to MapLibre LatLng.
 */

private fun List<Double>.toLatLng(): LatLng? {

    if (size < 2) {
        return null
    }

    return LatLng(
        this[1],
        this[0]
    )
}