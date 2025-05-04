package com.example.eventfinder.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfinder.databinding.FragmentDashboardBinding
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.ui.adapter.CategoryAdapter
import com.example.eventfinder.ui.adapter.EventAdapter
import com.example.eventfinder.ui.adapter.MyFavoriteEventAdapter
import com.example.eventfinder.viewmodel.FavoriteEventVM
import com.example.eventfinder.viewmodel.HomeViewModel

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    private val viewModel: FavoriteEventVM by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventsRecyclerView()
        observeViewModel()
        viewModel.fetchMyFavoriteEvents(requireContext()) // Add this line
    }

    private fun setupEventsRecyclerView() {
        binding.rvFavorites.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun observeViewModel() {
        viewModel.eventDetail.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse.isNotEmpty()) {
                binding.rvFavorites.adapter = MyFavoriteEventAdapter(
                    eventResponse,
                    onDeleteClick = { eventId ->
                        viewModel.deleteFavoriteEvent(requireContext(), eventId)
                    }
                )
                binding.rvFavorites.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.rvFavorites.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}