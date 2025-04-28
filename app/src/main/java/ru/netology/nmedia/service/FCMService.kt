package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val action = try {
            message.data["action"]?.let { Action.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            Action.UNKNOWN
        } ?: return

        when (action) {
            Action.LIKE -> handleLike(
                Gson().fromJson(
                    message.data["content"],
                    Like::class.java
                )
            )
            Action.NEWPOST -> handleNewPost(
                Gson().fromJson(
                    message.data["content"],
                    PostNotification::class.java
                )
            )
            Action.UNKNOWN -> println("Recieved unsupported push")
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    private fun handleNewPost(post: PostNotification) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_new_post,
                        post.postAuthor
                    )
                )
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(post.content))
                .build()

            NotificationManagerCompat
                .from(this)
                .notify(Random.nextInt(100_000), notification)

            return
        }
    }

    private fun handleLike(like: Like) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_user_liked,
                        like.userName,
                        like.postAuthor
                    )
                )
                .build()

            NotificationManagerCompat
                .from(this)
                .notify(Random.nextInt(100_000), notification)

            return
        }
    }

    companion object {
        private const val CHANNEL_ID = "notifications"
    }
}

enum class Action {
    LIKE, NEWPOST, UNKNOWN,
}

data class PostNotification(
    val userId: Int,
    val postAuthor: String,
    val content: String
)
data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)