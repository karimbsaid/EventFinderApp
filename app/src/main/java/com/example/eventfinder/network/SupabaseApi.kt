package com.example.eventfinder.network

import com.example.eventfinder.model.AuthRequest
import com.example.eventfinder.model.AuthResponse
import com.example.eventfinder.model.FavoriteItem
import com.example.eventfinder.model.FavoriteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @POST("auth/v1/signup")
    suspend fun register(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @GET("rest/v1/favorites")
    suspend fun getFavorites(
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): Response<List<FavoriteItem>>


    @GET("rest/v1/favorites")
    suspend fun getFavoriteById(
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*",
        @Query("event_id") eventId: String,
    ): Response<List<FavoriteItem>>


    @DELETE("rest/v1/favorites")
    suspend fun deleteFavorite(
        @Header("Authorization") authHeader: String,
        @Query("event_id") eventId: String
    ): Response<Unit>

    @POST("rest/v1/favorites")
    suspend fun addFavorite(
        @Header("Authorization") authHeader: String,
        @Body favoriteData: FavoriteRequest
    ): Response<Unit>
}