package com.example.eventfinder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfinder.utils.TokenManager

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val jwtToken = sharedPreferences.getString("jwt_token", null)

        if (jwtToken.isNullOrEmpty() || TokenManager.isTokenExpired(jwtToken)) {
            // No token -> go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // Token exists -> go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Finish this activity so it's not in the backstack
        finish()
    }
}
