package com.dedisive.foss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.dedisive.foss.ui.screen.RouteComparisonScreen
import com.dedisive.foss.ui.theme.FOSSTheme
import org.maplibre.android.MapLibre

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(this)

        setContent {
            FOSSTheme {
                RouteComparisonScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}