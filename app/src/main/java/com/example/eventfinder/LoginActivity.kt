package com.example.eventfinder

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.databinding.ActivityLoginBinding
import com.example.eventfinder.viewmodel.AuthentificationVM

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthentificationVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation to SignUp
        binding.goToSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Handle SignIn
        binding.signInButton.setOnClickListener {
            clearErrors()
            login()
        }

        binding.forgotPasswordText.setOnClickListener{
            startActivity(Intent(this,ForgetPasswordActivity::class.java))
        }

        // Observe login success
        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Welcome ${user.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // Observe errors
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                showGlobalError("Email or password is incorrect")
            }
        }

        // Hide field/global error when typing
        addTextWatchers()
    }

    private fun login() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        var hasError = false

        // Email validation
        if (email.isBlank()) {
            binding.emailError.text = "Email is required"
            binding.emailError.visibility = View.VISIBLE
            hasError = true
        }

        // Password validation
        if (password.isBlank()) {
            binding.passwordError.text = "Password is required"
            binding.passwordError.visibility = View.VISIBLE
            hasError = true
        } else if (password.length < 6) {
            binding.passwordError.text = "Password must be at least 6 characters"
            binding.passwordError.visibility = View.VISIBLE
            hasError = true
        }

        if (hasError) return

        viewModel.login(this, email, password)
    }

    private fun showGlobalError(message: String) {
        binding.globalError.text = message
        binding.globalError.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        binding.emailError.visibility = View.GONE
        binding.passwordError.visibility = View.GONE
        binding.globalError.visibility = View.GONE
    }

    private fun addTextWatchers() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.emailError.visibility = View.GONE
                binding.globalError.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.passwordError.visibility = View.GONE
                binding.globalError.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
