package com.example.eventfinder.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfinder.ui.EventDetail
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.FavoriteRequest
import com.example.eventfinder.ui.adapter.CategoryAdapter
import com.example.eventfinder.ui.adapter.EventAdapter
import com.example.eventfinder.viewmodel.MainViewModel

// HomeFragment.kt
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var detailLauncher: ActivityResultLauncher<Intent>

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                mainViewModel.fetchFavorites(requireContext()) {
                    mainViewModel.loadEvents()
                }
            }
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoriesRecyclerView()
        setupEventsRecyclerView()
        mainViewModel.initialize(requireContext())
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
        mainViewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.rvCategories.adapter = CategoryAdapter(categories) { clickedCategory ->
                mainViewModel.setSelectedCategory(clickedCategory.name)
                mainViewModel.loadEvents(clickedCategory.name)
            }

        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvEvents.visibility = if (isLoading) View.GONE else View.VISIBLE

            if (isLoading) {
                binding.shimmerLayout.startShimmer()
            } else {
                binding.shimmerLayout.stopShimmer()
            }
        }


        mainViewModel.events.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse != null) {
                val events = eventResponse._embedded?.events ?: emptyList()
                binding.rvEvents.adapter = EventAdapter(
                    events,
                    onJoinClick = { event -> },
                    onEventClick = { event -> openEventDetailActivity(event)  },
                    onFavoriteClick = { event ->
                        if (event.isFavorited) {
                            mainViewModel.removeFavoriteEvent(requireContext(), event.id,
                                onSuccess = {
                                    event.isFavorited = false
                                    binding.rvEvents.adapter?.notifyDataSetChanged()
                                },
                                onFailure = {
                                    Toast.makeText(requireContext(), "Failed to unfavorite event", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            val favoriteRequest:FavoriteRequest = FavoriteRequest(event.id,event.name)
                            mainViewModel.addFavoriteEvent(requireContext(), favoriteRequest,
                                onSuccess = {
                                    event.isFavorited = true
                                    binding.rvEvents.adapter?.notifyDataSetChanged()
                                },
                                onFailure = {
                                    Toast.makeText(requireContext(), "Failed to favorite event", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                )
            }
        }
    }
    private fun openEventDetailActivity(event: Event){
        val imageUrl = event.images.firstOrNull { it.ratio == "16_9" }?.url
        val intent = Intent(activity, EventDetail::class.java)
        intent.putExtra("EVENT_ID", event.id)
        intent.putExtra("EVENT_NAME", event.name)
        intent.putExtra("EVENT_THUMB", imageUrl)
        detailLauncher.launch(intent)
    }

}