package com.example.eventfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.databinding.ActivityLoginBinding
import com.example.eventfinder.viewmodel.AuthentificationVM

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Inject Supabase API into ViewModel
    private val viewModel: AuthentificationVM by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            login()
        }

        // Observe login success
        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Welcome ${user.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java)) // or navigate to HomeFragment
                finish()
            }
        }

        // Observe errors
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(this, email, password)
    }
}
