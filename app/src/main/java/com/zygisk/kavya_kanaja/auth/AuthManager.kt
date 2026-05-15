package com.zygisk.kavya_kanaja.auth

import android.content.Context
import android.net.Uri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)
    
    private val _userState = MutableStateFlow<User?>(auth.currentUser?.toUser())
    val userState = _userState.asStateFlow()

    data class User(
        val id: String,
        val email: String?,
        val displayName: String?,
        val photoUrl: String?
    )

    private fun com.google.firebase.auth.FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val directory = File(context.filesDir, "profile_pics")
            if (!directory.exists()) directory.mkdirs()

            // Delete existing profile pictures for this user to save space and avoid conflicts
            val userId = auth.currentUser?.uid ?: return@withContext null
            directory.listFiles { file -> file.name.startsWith("profile_$userId") }?.forEach { it.delete() }

            // Use timestamp to ensure Coil/Compose detects a "new" URI and refreshes the image
            val fileName = "profile_${userId}_${System.currentTimeMillis()}.jpg"
            val file = File(directory, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.toUri()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<User> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            val googleIdTokenCredential = when {
                credential is GoogleIdTokenCredential -> credential
                credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    try {
                        GoogleIdTokenCredential.createFrom(credential.data)
                    } catch (e: Exception) {
                        null
                    }
                }
                else -> null
            }

            if (googleIdTokenCredential != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user?.toUser() ?: throw Exception("Auth failed")
                _userState.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Unknown credential type: ${credential.type}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Sign up failed")
            
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
            
            val user = firebaseUser.toUser()
            _userState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user?.toUser() ?: throw Exception("Sign in failed")
            _userState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfilePicture(uri: Uri?): Result<User> {
        return try {
            val firebaseUser = auth.currentUser ?: throw Exception("User not logged in")
            
            val finalUri = if (uri != null) {
                saveImageToInternalStorage(uri)
            } else {
                // Remove local files if resetting profile pic
                val directory = File(context.filesDir, "profile_pics")
                directory.listFiles { file -> file.name.startsWith("profile_${firebaseUser.uid}") }?.forEach { it.delete() }
                null
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(finalUri)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
            
            val user = firebaseUser.toUser()
            _userState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<User> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val user = authResult.user?.toUser() ?: throw Exception("Guest sign in failed")
            _userState.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        _userState.value = null
    }
}
