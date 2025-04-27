package ru.netology.nmedia.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.data["action"]?.let {
            when (Action.valueOf(it)) {
                Action.LIKE -> handleLike(
                    Gson().fromJson(
                        message.data["content"],
                        Like::class.java
                    )
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    private fun handleLike(like: Like) {

    }
}

enum class Action {
    LIKE
}

data class Like(
    val userId: Int.Companion,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)