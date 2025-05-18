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
import com.example.eventfinder.utils.TokenManager
import com.example.eventfinder.utils.TokenManager.saveToken
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import org.json.JSONObject

class AuthentificationVM() : ViewModel() {

    private val _user = MutableLiveData<SupabaseUser?>()
    val user: LiveData<SupabaseUser?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun login(context: Context, email: String, password: String) {
        Log.d("username", email)
        Log.d("password", password)

        viewModelScope.launch {
            try {
                val response = SupabaseClient.api.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        _user.value = it.user
                        saveToken(context, it.access_token)
                        _error.value = null // Clear previous errors
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorMsg).getString("msg")
                    } catch (e: Exception) {
                        errorMsg ?: "Login failed"
                    }
                    _error.value = errorMessage
                }
            } catch (e: IOException) {
                _error.value = "Network error. Please check your connection."
            } catch (e: HttpException) {
                _error.value = "Login error: ${e.message()}"
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.localizedMessage}"
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
                    val errorMsg = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorMsg).getString("msg")
                    } catch (e: Exception) {
                        errorMsg ?: "Login failed"
                    }
                    _error.value = errorMessage
                }
            } catch (e: IOException) {
                _error.value = "Network error"
            } catch (e: HttpException) {
                _error.value = "Registration failed"
            }
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = TokenManager.getToken(context)
                if (token != null) {
                    val response = SupabaseClient.api.logout(
                        authHeader = "Bearer $token"
                    )
                    if (response.isSuccessful) {
                        TokenManager.clearToken(context)
                        onComplete()
                    } else {
                        Log.e("AuthentificationVM", "Logout failed: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthentificationVM", "Logout error", e)
            }
        }
    }



    fun getUserInfo(accessToken: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.api.getUser(
                    authorization = "Bearer $accessToken"
                )
                if (response.isSuccessful) {
                    _user.value = response.body()
                    _error.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    _error.value = errorMessage ?: "Failed to fetch user info"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                val response = SupabaseClient.api.sendPasswordReset(
                    redirectUrl = "myapp://reset-password",
                    email = mapOf("email" to email)
                )
                if (response.isSuccessful) {
                    _error.value = null // Success
                } else {
                    _error.value = "Failed to send reset email"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun resetPassword(accessToken: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $accessToken"
                val response = SupabaseClient.api.changePassword(
                    authHeader = authHeader,
                    password = mapOf("password" to newPassword)
                )

                if (response.isSuccessful) {
                    _error.value = null // success
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    _error.value = errorMessage ?: "Failed to reset password"
                }
            } catch (e: Exception) {
                _error.value = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        return try {
            val json = JSONObject(errorBody ?: return null)
            json.optString("msg") ?: json.optString("message")
        } catch (e: Exception) {
            null
        }
    }

}
