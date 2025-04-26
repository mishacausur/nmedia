package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.toURLOrNull
import java.net.MalformedURLException
import java.net.URL

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val isLiked: Boolean,
    val shares: Long,
    val views: Long,
    val likes: Long,
    val video: String?
) {
    fun toDTO() = Post(
        id,
        author,
        published,
        content,
        isLiked,
        shares.toUInt(),
        views.toUInt(),
        likes.toUInt(),
        video?.toURLOrNull()
    )

    companion object {
        fun fromDTO(post: Post) = PostEntity(
            post.id,
            post.author,
            post.published,
            post.content,
            post.isLiked,
            post.shares.toLong(),
            post.views.toLong(),
            post.likes.toLong(),
            post.video.toString()
        )
    }
}

