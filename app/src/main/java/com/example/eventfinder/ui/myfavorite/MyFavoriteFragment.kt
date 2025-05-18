package com.example.eventfinder.ui.myfavorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfinder.ui.EventDetail
import com.example.eventfinder.databinding.FragmentMyfavoriteeventsBinding
import com.example.eventfinder.model.Event
import com.example.eventfinder.ui.adapter.EventAdapter
import com.example.eventfinder.viewmodel.MainViewModel

class MyFavoriteFragment : Fragment() {

    private lateinit var binding: FragmentMyfavoriteeventsBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val favoriteEvents = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyfavoriteeventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchAndDisplayFavoriteEvents()
    }

    private fun setupRecyclerView() {
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun fetchAndDisplayFavoriteEvents() {
        mainViewModel.fetchFavorites(requireContext()) {
            val ids = mainViewModel.favoriteEventIds.value ?: emptySet()
            if (ids.isEmpty()) {
                showEmptyState()
                return@fetchFavorites
            }

            favoriteEvents.clear()
            var loadedCount = 0

            ids.forEach { eventId ->
                mainViewModel.fetchEventDetail(eventId) { event ->
                    loadedCount++
                    event?.let { favoriteEvents.add(it) }

                    if (loadedCount == ids.size) {
                        updateUI()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        if (favoriteEvents.isNotEmpty()) {
            binding.rvFavorites.adapter = EventAdapter(
                eventList = favoriteEvents,
                onJoinClick = {}, // You can implement this if needed
                onEventClick = { event ->
                    openEventDetailActivity(event)
                    // Handle event click (navigate to detail screen)
                    // Example:
                    // val intent = Intent(requireContext(), EventDetailActivity::class.java)
                    // intent.putExtra("event_id", event.id)
                    // startActivity(intent)
                },
                onFavoriteClick = { event ->
                    mainViewModel.removeFavoriteEvent(
                        context = requireContext(),
                        eventId = event.id,
                        onSuccess = {
                            fetchAndDisplayFavoriteEvents()
                        },
                        onFailure = {
                            // Show a toast or log failure
                        }
                    )
                }
            )
            binding.rvFavorites.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        } else {
            showEmptyState()
        }
    }
    private fun openEventDetailActivity(event: Event){
        val imageUrl = event.images.firstOrNull { it.ratio == "16_9" }?.url
        val intent = Intent(activity, EventDetail::class.java)
        intent.putExtra("EVENT_ID", event.id)
        intent.putExtra("EVENT_NAME", event.name)
        intent.putExtra("EVENT_THUMB", imageUrl)
        startActivity(intent)
    }
    private fun showEmptyState() {
        binding.rvFavorites.visibility = View.GONE
        binding.tvEmpty.visibility = View.VISIBLE
    }
}
