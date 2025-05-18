package com.example.eventfinder.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.databinding.ActivityResetPasswordBinding
import com.example.eventfinder.viewmodel.AuthentificationVM

class ResetPasswordActivity : AppCompatActivity() {
    private val authentificationVM: AuthentificationVM by viewModels()
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Get access_token from deep link fragment (e.g., #access_token=...)
        val fragment = intent?.data?.fragment // gets the part after "#"
        val token = fragment
            ?.split("&")
            ?.find { it.startsWith("access_token=") }
            ?.substringAfter("=")
        Log.d("tokenaccess",token.toString())

        binding.resetButton.setOnClickListener {
            val newPassword = binding.newPasswordEditText.text.toString()
            if (!token.isNullOrBlank()) {
                authentificationVM.resetPassword(token, newPassword)
            } else {
                // Token is missing
                Toast.makeText(this, "Invalid or missing reset token", Toast.LENGTH_LONG).show()
            }
        }
    }

}