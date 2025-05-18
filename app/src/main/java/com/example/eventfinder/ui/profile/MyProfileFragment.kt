package com.example.eventfinder.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.eventfinder.R
import com.example.eventfinder.ui.LoginActivity
import com.example.eventfinder.databinding.FragmentProfileBinding
import com.example.eventfinder.utils.TokenManager
import com.example.eventfinder.viewmodel.AuthentificationVM
import com.example.eventfinder.viewmodel.MainViewModel

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val authVM: AuthentificationVM by viewModels()
    private val mainVM: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val context = requireContext()
        val token = TokenManager.getToken(context)

        // Get user email
        if (!token.isNullOrEmpty()) {
            authVM.getUserInfo(token)
            mainVM.fetchFavorites(context) // Load user's favorite data
        }

//        binding.favorites.setOnClickListener{
//            val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
//            navController.navigate(R.id.navigation_favorites)
//        }
        binding.favorites.setOnClickListener {
            val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
            bottomNav.selectedItemId = R.id.navigation_favorites
        }

        binding.logoutBtn.setOnClickListener {
            authVM.logout(requireContext()) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        // Set email
        authVM.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userEmail.text = it.email
            }
        }

        // Display favorite count
        mainVM.favoriteEventIds.observe(viewLifecycleOwner) { favorites ->
            binding.countfavorite.text = favorites.size.toString()
        }

        // Error handling (optional)
        authVM.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                binding.userEmail.text = "Error: $it"
            }
        }

        return binding.root
    }
}
