package com.example.eventfinder.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.BuildConfig
import com.example.eventfinder.model.Categories
import com.example.eventfinder.model.Category
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.EventResponse
import com.example.eventfinder.model.FavoriteItem
import com.example.eventfinder.model.FavoriteRequest
import com.example.eventfinder.network.TicketMasterClient
import com.example.eventfinder.network.SupabaseClient
import com.example.eventfinder.utils.TokenManager
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    private val _events = MutableLiveData<EventResponse>()
    val events: LiveData<EventResponse> = _events

    private val _favoriteEvents = MutableLiveData<List<FavoriteItem>?>()
    val favoriteEvents: MutableLiveData<List<FavoriteItem>?> = _favoriteEvents

    val apiKey = BuildConfig.TICKETMASTER_API_KEY



    init {
        loadCategories()
        loadEvents(null)
    }

    private fun loadCategories() {
        _categories.value = Categories.getDefaultCategories()
    }

public fun loadEvents(categoryName: String?) {
    viewModelScope.launch {
        try {
            val response = TicketMasterClient.api.getEvents(
                latlong = "40.7128,-74.0060",
                category = categoryName,
                apiKey = apiKey
            )

            _events.value = response

            response._embedded?.events?.forEach { event ->
                Log.d("HomeViewModel", "Event loaded: ${event.name}")
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to load events: ", e)
        }
    }
}
    fun addFavoriteEvent(context: Context, event: Event) {
        Log.d("EventDetailVM", "Adding favorite with eventId: ${event}")
        Log.d("token", "${TokenManager.getToken(context)}")
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
//                        _isFavorited.value = true
//                        _errorMessage.value = "Added to favorites"
                    } else {
//                        _errorMessage.value = "Failed to add favorite: ${response.code()}"
                        Log.e("EventDetailVM", "Failed to add favorite: ${response.code()} ${response.message()}")
                    }
                } else {
//                    _errorMessage.value = "No JWT token found"
                    Log.e("EventDetailVM", "No JWT token found")
                }
            } catch (e: Exception) {
//                _errorMessage.value = "Error adding favorite: ${e.message}"
                Log.e("EventDetailVM", "Exception adding favorite: ${e.message}", e)
            } finally {
//                _isLoading.value = false
            }
        }
    }
     fun openTicketmasterLink(context: Context,event : Event ) {
        val ticketmasterUrl = event.url
        if (ticketmasterUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ticketmasterUrl))
            startActivity(context,intent,null)

        }
    }
    fun updateFavoriteStatus(favoriteEventIds: List<FavoriteItem>) {
        _events.value?._embedded?.events?.forEach { event ->
            event.isFavorited = favoriteEventIds.any { it.event_id == event.id }
        }

    }
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
                        _favoriteEvents.value = favorites ?: emptyList()
                        favorites?.let {
                            updateFavoriteStatus(it)
                        }
                    } else {
                        _favoriteEvents.value = emptyList()
                        Log.e("FavoriteEventVM", "Failed to load favorites: ${response.code()} ${response.message()}")
                    }
                } else {
                    _favoriteEvents.value = emptyList()
                    Log.e("FavoriteEventVM", "Token is null")
                }
            } catch (e: Exception) {
                _favoriteEvents.value = emptyList()
                Log.e("FavoriteEventVM", "Exception loading favorites: ${e.message}", e)
            }
        }
    }





}