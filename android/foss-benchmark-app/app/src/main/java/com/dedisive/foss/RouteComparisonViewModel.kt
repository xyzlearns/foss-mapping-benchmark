package com.dedisive.foss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedisive.foss.api.RetrofitClient
import com.dedisive.foss.model.RouteRequest
import com.dedisive.foss.state.RouteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteComparisonViewModel : ViewModel() {

    private val api = RetrofitClient.routeApi

    private val _uiState =
        MutableStateFlow(RouteUiState())

    val uiState: StateFlow<RouteUiState> =
        _uiState.asStateFlow()

    fun compare(
        origin: String,
        destination: String
    ) {

        if (origin.isBlank() || destination.isBlank()) {

            _uiState.value =
                RouteUiState(
                    error = "Enter both an origin and destination."
                )

            return
        }

        viewModelScope.launch {

            _uiState.value =
                RouteUiState(
                    isLoading = true
                )

            runCatching {

                api.compare(
                    RouteRequest(
                        startAddress = origin.trim(),
                        endAddress = destination.trim()
                    )
                )

            }.onSuccess { result ->

                _uiState.value =
                    RouteUiState(
                        comparison = result
                    )

            }.onFailure { exception ->

                _uiState.value =
                    RouteUiState(
                        error =
                            exception.message
                                ?: "Unable to load routes."
                    )
            }
        }
    }
}