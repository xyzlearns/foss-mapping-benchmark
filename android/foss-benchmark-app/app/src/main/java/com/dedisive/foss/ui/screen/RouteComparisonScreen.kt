package com.dedisive.foss.ui.screen

import com.dedisive.foss.RouteComparisonViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dedisive.foss.map.RouteMap
import com.dedisive.foss.model.RouteComparisonResponse
import com.dedisive.foss.model.RouteResult
import java.util.Locale

@Composable
fun RouteComparisonScreen(
    modifier: Modifier = Modifier,
    viewModel: RouteComparisonViewModel = viewModel()
) {

    val state by
    viewModel.uiState
        .collectAsStateWithLifecycle()

    var origin by remember {
        mutableStateOf(
            "India Gate, New Delhi"
        )
    }

    var destination by remember {
        mutableStateOf(
            "Red Fort, New Delhi"
        )
    }

    Box(modifier = modifier) {

        /*
         * Map
         */

        RouteMap(
            comparison = state.comparison,
            modifier = Modifier.fillMaxSize()
        )

        /*
         * Search card
         */

        Card(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(16.dp)
                .fillMaxWidth(),

            shape = RoundedCornerShape(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = origin,
                    onValueChange = {
                        origin = it
                    },
                    label = {
                        Text("Origin")
                    },
                    singleLine = true,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                    },
                    label = {
                        Text("Destination")
                    },
                    singleLine = true,
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.compare(
                            origin,
                            destination
                        )
                    },

                    enabled = !state.isLoading,

                    modifier =
                        Modifier.align(
                            Alignment.End
                        )
                ) {

                    if (state.isLoading) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.width(18.dp),

                            strokeWidth = 2.dp
                        )

                    } else {

                        Text("Compare routes")
                    }
                }

                state.error?.let { error ->

                    Text(
                        text = error,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }
            }
        }

        /*
         * Comparison information
         */

        state.comparison?.let { comparison ->

            ComparisonPanel(
                comparison = comparison,

                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
                    .fillMaxWidth()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surface
                            .copy(alpha = 0.96f)
                    )
            )
        }
    }
}

@Composable
private fun ComparisonPanel(
    comparison: RouteComparisonResponse,
    modifier: Modifier = Modifier
) {

    val routes =
        listOf(
            "OSRM" to comparison.osrm,
            "Valhalla" to comparison.valhalla,
            "Google" to comparison.google
        )

    Column(
        modifier = modifier
            .safeDrawingPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {

        Text(
            text = "Route comparison",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        routes.forEach { (name, route) ->

            RouteRow(
                name = name,
                route = route
            )
        }

        val baseline =
            comparison.google

        Text(
            text =
                "Difference from Google: " +
                        "OSRM ${formatPercent(
                            comparison.osrm.distance,
                            baseline.distance
                        )} / ${formatPercent(
                            comparison.osrm.duration,
                            baseline.duration
                        )}; " +

                        "Valhalla ${formatPercent(
                            comparison.valhalla.distance,
                            baseline.distance
                        )} / ${formatPercent(
                            comparison.valhalla.duration,
                            baseline.duration
                        )}",

            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )
    }
}

@Composable
private fun RouteRow(
    name: String,
    route: RouteResult
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(name)

        Text(
            text =
                "${formatDistance(route.distance)} km" +
                        " · " +
                        formatDuration(route.duration)
        )
    }
}

private fun formatDistance(
    distance: Double
): String {

    return String.format(
        Locale.US,
        "%.2f",
        distance
    )
}

private fun formatDuration(
    seconds: Double
): String {

    val totalSeconds =
        seconds.toInt()

    val minutes =
        totalSeconds / 60

    val remainingSeconds =
        totalSeconds % 60

    return if (remainingSeconds == 0) {

        "$minutes min"

    } else {

        "$minutes min ${remainingSeconds}s"
    }
}

private fun formatPercent(
    value: Double,
    baseline: Double
): String {

    if (baseline == 0.0) {
        return "—"
    }

    return String.format(
        Locale.US,
        "%+.1f%%",
        (value - baseline) /
                baseline *
                100
    )
}