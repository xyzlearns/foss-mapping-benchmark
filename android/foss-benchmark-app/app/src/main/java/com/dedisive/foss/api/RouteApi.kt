package com.dedisive.foss.api

import com.dedisive.foss.model.RouteComparisonResponse
import com.dedisive.foss.model.RouteRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RouteApi {

    @POST("api/routes/compare")
    suspend fun compare(
        @Body request: RouteRequest
    ): RouteComparisonResponse
}