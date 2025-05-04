package com.example.eventfinder.model

data class FavoriteItem(
    val id: Int,
    val user_id: String,
    val event_id: String,
    val event_name: String,
    val created_at: String
)

