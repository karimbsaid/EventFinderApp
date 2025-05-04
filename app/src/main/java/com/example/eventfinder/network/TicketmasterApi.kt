package com.example.eventfinder.network

// network/TicketmasterApi.kt
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketmasterApi {
    @GET("discovery/v2/events.json")
    suspend fun getEvents(
        @Query("latlong") latlong: String,
        @Query("classificationName") category: String?,
        @Query("apikey") apiKey: String
    ): EventResponse

    @GET("discovery/v2/events/{id}.json")
    suspend fun getEventById(
        @Path("id") id: String,
        @Query("apikey") apiKey: String
    ): Event
}