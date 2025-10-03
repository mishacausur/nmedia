package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
        private var INSTANCE: AppAuth? = null

        fun init(context: Context) {
            INSTANCE = AppAuth(context)
        }

        fun getInstance() = requireNotNull(INSTANCE) {
            "Need to be initialazed first"
        }
    }

    private val preferences = context.applicationContext.getSharedPreferences(
        "auth",
        Context.MODE_PRIVATE
    )

    private val _data: MutableStateFlow<Token?>

    init {
        val _id = preferences.getLong(ID_KEY, 0L)
        val _token = preferences.getString(TOKEN_KEY, null)

        if (_id == 0L || _token == null) {
            preferences.edit { clear() }
            _data = MutableStateFlow(null)
        } else {
            _data = MutableStateFlow(Token(_id, _token))
        }
    }

    val data = _data.asStateFlow()

    fun setAuth(id: Long, token: String) {
        preferences.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }

        _data.value = Token(id, token)
    }

    fun unauth() {
        preferences.edit { clear() }
        _data.value = null
    }
}