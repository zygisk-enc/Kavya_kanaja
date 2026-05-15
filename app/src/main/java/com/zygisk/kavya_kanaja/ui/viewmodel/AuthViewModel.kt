package com.zygisk.kavya_kanaja.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zygisk.kavya_kanaja.auth.AuthManager
import com.zygisk.kavya_kanaja.data.local.UserProfileDao
import com.zygisk.kavya_kanaja.data.local.UserProfileEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: AuthManager,
    private val userProfileDao: UserProfileDao
) : ViewModel() {
    val userState: StateFlow<AuthManager.User?> = authManager.userState

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: Flow<UserProfileEntity?> = userState.flatMapLatest { user ->
        if (user != null) {
            userProfileDao.getUserProfile(user.id)
        } else {
            flowOf(null)
        }
    }

    fun saveUserProfile(name: String, gender: String, phoneNumber: String) {
        val user = userState.value ?: return
        viewModelScope.launch {
            val profile = UserProfileEntity(
                userId = user.id,
                name = name,
                gender = gender,
                phoneNumber = phoneNumber
            )
            userProfileDao.insertOrUpdateProfile(profile)
        }
    }

    fun signInWithGoogle(context: Context, webClientId: String, onResult: (Result<AuthManager.User>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(context, webClientId)
            onResult(result)
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String, onResult: (Result<AuthManager.User>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signUpWithEmail(email, password, name)
            onResult(result)
        }
    }

    fun signInWithEmail(email: String, password: String, onResult: (Result<AuthManager.User>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signInWithEmail(email, password)
            onResult(result)
        }
    }

    fun signInAnonymously(onResult: (Result<AuthManager.User>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signInAnonymously()
            onResult(result)
        }
    }

    fun updateProfilePicture(uri: Uri?, onResult: (Result<AuthManager.User>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.updateProfilePicture(uri)
            onResult(result)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
        }
    }
}
