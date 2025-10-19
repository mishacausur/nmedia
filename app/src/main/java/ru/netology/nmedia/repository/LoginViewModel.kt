package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthApi
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

data class LoginState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val authApi: AuthApi
) : ViewModel() {
    private val _state = MutableLiveData(LoginState())
    val state: LiveData<LoginState> = _state

    fun login(login: String, pass: String) {
        if (login.isBlank() || pass.isBlank()) {
            _state.value = LoginState(error = "Enter your credentials")
            return
        }
        _state.value = LoginState(loading = true)
        viewModelScope.launch {
            try {
                val token = authApi.authenticate(login, pass)
                appAuth.setAuth(token.id, token.token)
                _state.value = LoginState(success = true)
            } catch (e: Exception) {
                _state.value = LoginState(error = e.message ?: "Auth error occurred")
            }
        }
    }
}



