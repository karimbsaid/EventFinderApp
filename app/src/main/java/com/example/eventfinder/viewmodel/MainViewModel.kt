package com.example.eventfinder.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.BuildConfig
import com.example.eventfinder.model.*
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _selectedCategory = MutableLiveData("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    val apiKey = BuildConfig.TICKETMASTER_API_KEY

    fun initialize(context: Context, categoryName: String = "All") {
        fetchFavorites(context) {
            loadEvents(categoryName)
        }
        loadCategories()
    }

    private fun launchWithLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                block()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun fetchFavorites(context: Context, onComplete: () -> Unit = {}) {
        launchWithLoading {
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
                onComplete()
            }
        }
    }

    fun loadEvents(categoryName: String? = _selectedCategory.value) {
        launchWithLoading {
            Log.d("_selectedCategory",_selectedCategory.value.toString())
            try {
                val categoryToUse = if (categoryName == "All") null else categoryName
                _selectedCategory.value = categoryName ?: "All"
                Log.d("categoryToUse",categoryToUse.toString())
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
                Log.e("MainViewModel", "Failed to load events", e)
            }
        }
    }


    fun addFavoriteEvent(
        context: Context,
        favorite: FavoriteRequest,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        launchWithLoading {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.addFavorite(authHeader, favorite)
                    if (response.isSuccessful) {
                        val updatedSet = _favoriteEventIds.value?.toMutableSet() ?: mutableSetOf()
                        updatedSet.add(favorite.event_id)
                        _favoriteEventIds.value = updatedSet
                        loadEvents()
                        onSuccess()
                    } else {
                        Log.d("addFavoriteEvent", "Failed response: ${response.code()}")
                        onFailure()
                    }
                } else {
                    Log.e("MainViewModel", "No JWT token found")
                    onFailure()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception adding favorite: ${e.message}", e)
                onFailure()
            }
        }
    }

    fun removeFavoriteEvent(
        context: Context,
        eventId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        launchWithLoading {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.deleteFavorite(authHeader, "eq.${eventId}")
                    if (response.isSuccessful) {
                        val updatedSet = _favoriteEventIds.value?.toMutableSet() ?: mutableSetOf()
                        updatedSet.remove(eventId)
                        _favoriteEventIds.value = updatedSet
                        loadEvents()
                        onSuccess()
                    } else {
                        onFailure()
                    }
                } else {
                    Log.e("MainViewModel", "No JWT token found")
                    onFailure()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception removing favorite: ${e.message}", e)
                onFailure()
            }
        }
    }

    fun fetchEventDetail(eventId: String, onResult: (Event?) -> Unit) {
        launchWithLoading {
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

    fun isFavorite(context: Context, eventId: String, onResult: (FavoriteItem?) -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val authHeader = "Bearer $token"
                    val response = SupabaseClient.api.getFavoriteById(authHeader, eventId = "eq.$eventId")
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
                Log.e("MainViewModel", "Failed to load favorite", e)
                onResult(null)
            }
        }
    }

    private fun loadCategories() {
        _categories.value = Categories.getDefaultCategories()
    }
}
