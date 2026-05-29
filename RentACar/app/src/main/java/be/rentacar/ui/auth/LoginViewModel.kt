package be.rentacar.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import be.rentacar.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "LoginViewModel"

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: FirebaseUser) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // probeer automatisch in te loggen met de bewaarde Google account
    fun attemptSilentSignIn(client: GoogleSignInClient) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val account = client.silentSignIn().await()
                val idToken = account.idToken
                if (idToken != null) {
                    Log.d(TAG, "Silent sign-in successful for ${account.email}")
                    handleSignInResult(idToken)
                } else {
                    Log.d(TAG, "Silent sign-in: idToken null → fallback naar interactief")
                    _uiState.value = LoginUiState.Idle
                }
            } catch (e: Exception) {
                // geen of verlopen cache - gewone login knop tonen
                Log.d(TAG, "Silent sign-in not available: ${e.message}")
                _uiState.value = LoginUiState.Idle
            }
        }
    }

    fun handleSignInResult(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = AuthManager.signInWithGoogle(idToken)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = LoginUiState.Success(user)
                    Log.d(TAG, "Logged in: ${user.email}")
                },
                onFailure = { e ->
                    _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
                    Log.e(TAG, "Login failed", e)
                }
            )
        }
    }

    fun setLoading() {
        _uiState.value = LoginUiState.Loading
    }

    fun resetToIdle() {
        _uiState.value = LoginUiState.Idle
    }

    fun setError(message: String) {
        _uiState.value = LoginUiState.Error(message)
        Log.e(TAG, "Error set: $message")
    }
}
