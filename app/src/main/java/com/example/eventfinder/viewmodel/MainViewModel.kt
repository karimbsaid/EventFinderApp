package com.example.eventfinder.viewmodel

import android.content.Context
import android.util.Log
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
import com.example.eventfinder.network.SupabaseClient
import com.example.eventfinder.network.TicketMasterClient
import com.example.eventfinder.utils.TokenManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    private val _events = MutableLiveData<EventResponse>()
    val events: LiveData<EventResponse> = _events
    private val _favoriteEventIds = MutableLiveData<Set<String>>()
    val favoriteEventIds: LiveData<Set<String>> = _favoriteEventIds
    val apiKey = BuildConfig.TICKETMASTER_API_KEY
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun initialize(context: Context, categoryName: String = "All") {
        fetchFavorites(context) {
            loadEvents(categoryName)
        }
        loadCategories()
    }

    fun fetchFavorites(context: Context, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.getFavorites(authHeader)
                    if (response.isSuccessful) {
                        val favorites = response.body() ?: emptyList()
                        _favoriteEventIds.value = favorites.map { it.event_id }.toSet()
                    } else {
                        Log.e("MainViewModel", "Failed to fetch favorites: ${response.code()}")
                        _favoriteEventIds.value = emptySet()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching favorites", e)
                _favoriteEventIds.value = emptySet()
            } finally {
                _isLoading.value=false
                onComplete()
            }
        }
    }
    fun removeFavoriteEvent(
        context: Context,
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.deleteFavorite(authHeader, "eq.${eventId}")
                    if (response.isSuccessful) {
                        val updatedSet = _favoriteEventIds.value?.toMutableSet() ?: mutableSetOf()
                        updatedSet.remove(eventId)
                        _favoriteEventIds.value = updatedSet
                        loadEvents("All")
                        onSuccess()
                    } else {
                        onFailure()
                    }
                } else {
                    Log.e("MainViewModel", "No JWT token found")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception removing favorite: ${e.message}", e)
            }
        }
    }


    fun loadEvents(categoryName: String?) {
        viewModelScope.launch {
            Log.d("loadevents",categoryName.toString())
            try {
                val categoryToUse = if (categoryName == "All") null else categoryName
                Log.d("categoryToUse", categoryToUse.toString())
                val response = TicketMasterClient.api.getEvents(
                    latlong = "40.7128,-74.0060",
                    category = categoryToUse,
                    apiKey = apiKey
                )

                val favoriteIds = _favoriteEventIds.value ?: emptySet()
                response._embedded?.events?.forEach { event ->
                    event.isFavorited = favoriteIds.contains(event.id)
                }

                _events.value = response
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load events: ", e)
            }
        }
    }

    fun isFavorite(context: Context, eventId: String, onResult: (FavoriteItem?) -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                Log.d("favoriteeventid",eventId)
                Log.d("token", token.toString())

                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.getFavoriteById(authHeader, eventId = "eq.$eventId")
                    Log.d("responseFavoriteEvenetid",response.toString())

                    if (response.isSuccessful) {
                        val items = response.body()
                        val favorite = items?.firstOrNull()
                        onResult(favorite)
                    } else {
                        onResult(null)
                    }

                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load favorite: ", e)
                onResult(null)
            }
        }
    }




    private fun loadCategories() {
        _categories.value = Categories.getDefaultCategories()
    }

    fun addFavoriteEvent(context: Context, favorite: FavoriteRequest ,  onSuccess: () -> Unit,
                         onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"

                    val response = SupabaseClient.api.addFavorite(authHeader, favorite)
                    if (response.isSuccessful) {
                        val updatedSet = _favoriteEventIds.value?.toMutableSet() ?: mutableSetOf()
                        Log.d("updatedSet",updatedSet.toString())
                        updatedSet.add(favorite.event_id)
                        _favoriteEventIds.value = updatedSet
                        Log.d("myeventidfavoritee",_favoriteEventIds.toString())
                        loadEvents("All") // reload with updated favorites
                       onSuccess()
                    } else {
//                        _errorMessage.value = "Failed to add favorite: ${response.code()}"
                        Log.d("failureaddfavorite",response.toString())
                        onFailure()
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
    fun fetchEventDetail(eventId: String, onResult: (Event?) -> Unit) {
        viewModelScope.launch {
            try {
                val eventDetail = TicketMasterClient.api.getEventById(eventId, apiKey)
                eventDetail.isFavorited = _favoriteEventIds.value?.contains(eventId) == true
                onResult(eventDetail)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to fetch event detail for $eventId", e)
                onResult(null)
            }
        }
    }


}