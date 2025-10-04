package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.AuthService
import ru.netology.nmedia.auth.AppAuth

data class LoginState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
)

class LoginViewModel : ViewModel() {
    private val _state = MutableLiveData(LoginState())
    val state: LiveData<LoginState> = _state

    fun login(login: String, pass: String) {
        if (login.isBlank() || pass.isBlank()) {
            _state.value = LoginState(error = "Enter your credentioals")
            return
        }
        _state.value = LoginState(loading = true)
        viewModelScope.launch {
            try {
                val token = AuthService.service.authenticate(login, pass)
                AppAuth.getInstance().setAuth(token.id, token.token)
                _state.value = LoginState(success = true)
            } catch (e: Exception) {
                _state.value = LoginState(error = e.message ?: "Auth error occured")
            }
        }
    }
}


