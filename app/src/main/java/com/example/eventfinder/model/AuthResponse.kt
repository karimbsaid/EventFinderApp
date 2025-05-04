package com.example.eventfinder.model

data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val refresh_token: String,
    val user: SupabaseUser
)

data class SupabaseUser(
    val id: String,
    val email: String,
    val created_at: String
)
