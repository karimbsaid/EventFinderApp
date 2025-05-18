package com.example.eventfinder.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.R
import com.example.eventfinder.databinding.ActivityForgetPasswordBinding
import com.example.eventfinder.viewmodel.AuthentificationVM

class ForgetPasswordActivity : AppCompatActivity() {
    private val authentificationVM: AuthentificationVM by viewModels()
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendResetEmailButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            if (email.isNotEmpty()) {
                authentificationVM.sendPasswordResetEmail(email)
                binding.messageTextView.setTextColor(getColor(R.color.success_green)) // define in colors.xml
                binding.messageTextView.text = "Reset email sent if account exists."
            } else {
                binding.messageTextView.setTextColor(getColor(R.color.error_red)) // define in colors.xml
                binding.messageTextView.text = "Please enter your email."
            }
        }
    }
}
