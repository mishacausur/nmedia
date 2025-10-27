package ru.netology.nmedia.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.dto.Token
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext context: Context
) {

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
    }

    private val preferences = context.applicationContext.getSharedPreferences(
        "auth",
        Context.MODE_PRIVATE
    )

    private val _data: MutableStateFlow<Token?>

    init {
        val id = preferences.getLong(ID_KEY, 0L)
        val token = preferences.getString(TOKEN_KEY, null)

        if (id == 0L || token == null) {
            preferences.edit().clear().apply()
            _data = MutableStateFlow(null)
        } else {
            _data = MutableStateFlow(Token(id, token))
        }
    }

    val data = _data.asStateFlow()

    fun setAuth(id: Long, token: String) {
        preferences.edit()
            .putLong(ID_KEY, id)
            .putString(TOKEN_KEY, token)
            .apply()

        _data.value = Token(id, token)
    }

    fun unauth() {
        preferences.edit().clear().apply()
        _data.value = null
    }
}