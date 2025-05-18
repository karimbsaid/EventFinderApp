package com.example.eventfinder.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.eventfinder.LoginActivity
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.databinding.FragmentNotificationsBinding
import com.example.eventfinder.utils.TokenManager
import com.example.eventfinder.viewmodel.AuthentificationVM
import com.example.eventfinder.viewmodel.MainViewModel

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private val authVM: AuthentificationVM by viewModels()
    private val mainVM: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        val context = requireContext()
        val token = TokenManager.getToken(context)

        // Get user email
        if (!token.isNullOrEmpty()) {
            authVM.getUserInfo(token)
            mainVM.fetchFavorites(context) // Load user's favorite data
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
