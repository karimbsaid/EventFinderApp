package com.example.eventfinder.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.databinding.ActivityRegisterBinding
import com.example.eventfinder.viewmodel.AuthentificationVM

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthentificationVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aller à l'activité de connexion
        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Inscription
        binding.signUpButton.setOnClickListener {
            register()
        }

        // Observateur utilisateur
        viewModel.user.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Compte créé: ${user.email}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        // Observateur erreur
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                showGlobalError(it)
            }
        }
    }

    private fun register() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.passwordConfirmEt.text.toString()

        // Réinitialiser l'affichage des erreurs
        binding.emailError.visibility = View.GONE
        binding.passwordError.visibility = View.GONE
        binding.passwordConfirmError.visibility = View.GONE
        binding.globalError.visibility = View.GONE

        var hasError = false

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailError.visibility = View.VISIBLE
            hasError = true
        }

        if (password.length < 6) {
            binding.passwordError.visibility = View.VISIBLE
            hasError = true
        }

        if (password != confirmPassword) {
            binding.passwordConfirmError.visibility = View.VISIBLE
            binding.passwordConfirmError.text = "Passwords do not match"
            hasError = true
        }

        if (hasError) return

        // Appel ViewModel
        viewModel.register(this, email, password)
    }

    private fun showGlobalError(message: String) {
        binding.globalError.text = message
        binding.globalError.visibility = View.VISIBLE
    }
}
