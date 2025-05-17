package com.example.eventfinder.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.BuildConfig
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.FavoriteRequest
import com.example.eventfinder.network.TicketMasterClient
import com.example.eventfinder.network.SupabaseClient
import com.example.eventfinder.utils.TokenManager
import kotlinx.coroutines.launch

class EventDetailVM : ViewModel() {
    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isFavorited = MutableLiveData<Boolean>()
    val isFavorited: LiveData<Boolean> = _isFavorited

    private val apiKey = BuildConfig.TICKETMASTER_API_KEY

    fun fetchDetailById(eventId: String) {
        Log.d("EventDetailVM", "Fetching event with ID: $eventId")
        viewModelScope.launch {
            try {
                val event = TicketMasterClient.api.getEventById(
                    id = eventId,
                    apiKey = apiKey
                )
                _eventDetail.value = event
                Log.d("EventDetailVM", "Event loaded: ${event.name}")
            } catch (e: Exception) {
                _eventDetail.value = null
                Log.e("EventDetailVM", "Failed to load event: ${e.message}", e)
            }
        }
    }

    fun addFavoriteEvent(context: Context, event: Event) {
        Log.d("EventDetailVM", "Adding favorite with eventId: ${event}")
        Log.d("token", "${TokenManager.getToken(context)}")
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val favorite = FavoriteRequest(
                        event_id = event.id,
                        event_name = event.name,
                    )
                    val response = SupabaseClient.api.addFavorite(authHeader, favorite)
                    if (response.isSuccessful) {
                        Log.d("EventDetailVM", "Favorite added successfully")
                        _isFavorited.value = true
                        _errorMessage.value = "Added to favorites"
                    } else {
                        _errorMessage.value = "Failed to add favorite: ${response.code()}"
                        Log.e("EventDetailVM", "Failed to add favorite: ${response.code()} ${response.message()}")
                    }
                } else {
                    _errorMessage.value = "No JWT token found"
                    Log.e("EventDetailVM", "No JWT token found")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding favorite: ${e.message}"
                Log.e("EventDetailVM", "Exception adding favorite: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}