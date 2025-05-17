package com.example.eventfinder.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfinder.EventDetail
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.model.Event
import com.example.eventfinder.ui.adapter.CategoryAdapter
import com.example.eventfinder.ui.adapter.EventAdapter
import com.example.eventfinder.viewmodel.HomeViewModel

// HomeFragment.kt
class `HomeFragment-v1` : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerView()
        setupEventsRecyclerView() // <-- new

        observeViewModel()
    }

    private fun setupEventsRecyclerView() {
        binding.rvEvents.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupCategoriesRecyclerView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun openEventDetailActivity(event: Event){
        val imageUrl = event.images.firstOrNull { it.ratio == "16_9" }?.url
        val intent = Intent(activity, EventDetail::class.java)
        intent.putExtra("EVENT_ID", event.id)
        intent.putExtra("EVENT_NAME", event.name)
        intent.putExtra("EVENT_THUMB", imageUrl)
        startActivity(intent)
    }

    private fun observeViewModel() {
        homeViewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.rvCategories.adapter = CategoryAdapter(categories, onCategoryClick = { clickedCategory ->
                homeViewModel.loadEvents(clickedCategory.name)
            })
        }

        homeViewModel.events.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse != null) {
                Log.d("eeeeeeeeeeeeeeeee", (eventResponse!=null).toString())
                homeViewModel.fetchMyFavoriteEvents(requireContext()) // Ensure this is called after events load
                val events = eventResponse._embedded?.events ?: emptyList()
                binding.rvEvents.adapter = EventAdapter(
                    events,
                    onJoinClick = { event -> homeViewModel.openTicketmasterLink(requireContext(), event) },
                    onEventClick = { event -> openEventDetailActivity(event) },
                    onFavoriteClick = { event -> homeViewModel.addFavoriteEvent(requireContext(), event) }
                )
            }
        }
    }

}