package com.dedisive.foss.state

import com.dedisive.foss.model.RouteComparisonResponse

data class RouteUiState(
    val isLoading: Boolean = false,
    val comparison: RouteComparisonResponse? = null,
    val error: String? = null
)