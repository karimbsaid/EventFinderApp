package com.example.eventfinder.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.BuildConfig
import com.example.eventfinder.model.Address
import com.example.eventfinder.model.Categories
import com.example.eventfinder.model.Category
import com.example.eventfinder.model.Classification
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.EventDates
import com.example.eventfinder.model.EventEmbedded
import com.example.eventfinder.model.EventResponse
import com.example.eventfinder.model.EventStart
import com.example.eventfinder.model.Segment
import com.example.eventfinder.model.Venue
import com.example.eventfinder.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    private val _events = MutableLiveData<EventResponse>()
    val events: LiveData<EventResponse> = _events
    val apiKey = BuildConfig.TICKETMASTER_API_KEY



    init {
        loadCategories()
        loadEvents(null)
    }

    private fun loadCategories() {
        _categories.value = Categories.getDefaultCategories()
    }

public fun loadEvents(categoryName: String?) {
    Log.d("search category query" , "query $categoryName")
    viewModelScope.launch {
        try {
            val response = RetrofitClient.api.getEvents(
                latlong = "40.7128,-74.0060", // Example: New York City
                category = categoryName, // or you can set a default category
                apiKey = apiKey // 🔥 put your real Ticketmaster API Key here
            )

            _events.value = response // The response is already an EventResponse with List<Event>

            // Optional: log each event name for debug
            response._embedded?.events?.forEach { event ->
                Log.d("HomeViewModel", "Event loaded: ${event.name}")
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to load events: ", e)
        }
    }
}



}