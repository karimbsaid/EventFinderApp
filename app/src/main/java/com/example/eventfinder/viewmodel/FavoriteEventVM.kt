package com.example.eventfinder.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.BuildConfig
import com.example.eventfinder.model.Event
import com.example.eventfinder.network.TicketMasterClient
import com.example.eventfinder.network.SupabaseClient
import com.example.eventfinder.utils.TokenManager
import kotlinx.coroutines.launch

class FavoriteEventVM : ViewModel() {
    private val _eventDetail = MutableLiveData<List<Event>>()
    val eventDetail: LiveData<List<Event>> = _eventDetail
    private val apiKey = BuildConfig.TICKETMASTER_API_KEY

    fun fetchMyFavoriteEvents(context: Context) {
        Log.d("FavoriteEventVM", "Starting fetchMyFavoriteEvents")
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.getFavorites(authHeader)
                    if (response.isSuccessful) {
                        val favorites = response.body()
                        if (!favorites.isNullOrEmpty()) {
                            val events = favorites.map { favorite ->
                                TicketMasterClient.api.getEventById(favorite.event_id, apiKey)
                            }
                            _eventDetail.value = events

                        } else {
                            _eventDetail.value = emptyList()
                            Log.d("FavoriteEventVM", "No favorites found")
                        }
                    } else {
                        _eventDetail.value = emptyList()
                        Log.e("FavoriteEventVM", "Failed to load favorites: ${response.code()} ${response.message()}")
                    }
                } else {
                    _eventDetail.value = emptyList()
                    Log.e("FavoriteEventVM", "Token is null")
                }
            } catch (e: Exception) {
                _eventDetail.value = emptyList()
                Log.e("FavoriteEventVM", "Exception loading favorites: ${e.message}", e)
            }
        }
    }


    fun deleteFavoriteEvent(context: Context, eventId: String) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.deleteFavorite(authHeader, eventId)

                    if (response.isSuccessful) {
                        fetchMyFavoriteEvents(context)
                    } else {
                        Log.e("FavoriteEventVM", "Failed to delete favorite: ${response.code()}")
                    }
                } else {
                    Log.e("FavoriteEventVM", "No JWT token found for delete")
                }
            } catch (e: Exception) {
                Log.e("FavoriteEventVM", "Exception deleting favorite: ", e)
            }
        }
    }
}
