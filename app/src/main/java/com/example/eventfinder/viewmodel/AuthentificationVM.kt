package com.example.eventfinder.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventfinder.model.AuthRequest
import com.example.eventfinder.model.SupabaseUser
import com.example.eventfinder.network.SupabaseApi
import com.example.eventfinder.network.SupabaseClient
import com.example.eventfinder.utils.TokenManager.saveToken
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthentificationVM() : ViewModel() {

    private val _user = MutableLiveData<SupabaseUser?>()
    val user: LiveData<SupabaseUser?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(context: Context, email: String, password: String) {
        Log.d("username",email)
        Log.d("password",password)
        viewModelScope.launch {
            try {
                val response = SupabaseClient.api.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        _user.value = it.user
                        saveToken(context, it.access_token)
                    }
                } else {
                    _error.value = "Login failed: ${response.code()}"
                }
            } catch (e: IOException) {
                _error.value = "Network error"
            } catch (e: HttpException) {
                _error.value = "Invalid email or password"
            }
        }
    }

    fun register(context: Context, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.api.register(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        _user.value = it.user
                        saveToken(context, it.access_token)
                    }
                } else {
                    _error.value = "Registration failed: ${response.code()}"
                }
            } catch (e: IOException) {
                _error.value = "Network error"
            } catch (e: HttpException) {
                _error.value = "Registration failed"
            }
        }
    }
}
