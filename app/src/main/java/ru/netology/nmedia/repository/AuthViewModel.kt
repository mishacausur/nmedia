package ru.netology.nmedia.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel: ViewModel() {
    val data = AppAuth.getInstance().data.asLiveData()
    val isAuth: Boolean
        get() = AppAuth.getInstance().data.value != null
}