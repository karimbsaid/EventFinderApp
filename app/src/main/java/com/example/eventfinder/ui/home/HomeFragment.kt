package com.example.eventfinder.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.ui.adapter.CategoryAdapter
import com.example.eventfinder.ui.adapter.EventAdapter
import com.example.eventfinder.viewmodel.HomeViewModel

// HomeFragment.kt
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

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

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.rvCategories.adapter = CategoryAdapter(categories,  onCategoryClick = { clickedCategory ->
                // when user clicks, update selectedCategoryId
                // and load events based on clickedCategory.name
                viewModel.loadEvents(clickedCategory.name)

            })
        }
        viewModel.events.observe(viewLifecycleOwner) { eventResponse ->
            val events = eventResponse._embedded?.events ?: emptyList()
            binding.rvEvents.adapter = EventAdapter(
                events,
                onJoinClick = { event ->
                    // Handle join button click
                    // Example:
                    // Toast.makeText(context, "Joining ${event.name}", Toast.LENGTH_SHORT).show()
                },
                onFavoriteClick = { event ->
                    // Handle favorite button click
                    // Example:
                    // Toast.makeText(context, "Favorited ${event.name}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}