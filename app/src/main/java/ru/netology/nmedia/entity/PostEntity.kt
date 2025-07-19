package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.toURLOrNull
import java.net.MalformedURLException
import java.net.URL
import com.google.gson.annotations.SerializedName

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val published: String,
    val content: String,
    @SerializedName("likedByMe")
    val isLiked: Boolean,
    val shares: Long,
    val views: Long,
    val likes: Long,
    val video: String?,
    val visible: Boolean = true
) {
    fun toDTO() = Post(
        id,
        author,
        authorAvatar,
        published,
        content,
        isLiked,
        shares.toUInt(),
        views.toUInt(),
        likes.toInt(),
        video?.toURLOrNull(),
        visible
    )

    companion object {
        fun fromDTO(post: Post, visible: Boolean = true) = PostEntity(
            post.id,
            post.author,
            post.authorAvatar,
            post.published,
            post.content,
            post.isLiked,
            post.shares.toLong(),
            post.views.toLong(),
            post.likes.toLong(),
            post.video.toString(),
            visible = visible
        )
    }
}

data class PostResponseDto(
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val published: String,
    val content: String,
    val isLiked: Boolean,
    val shares: Long,
    val views: Long,
    val likes: Long,
    val video: String?,
    val visible: Boolean = true
) {
    fun toEntity(visible: Boolean = true) = PostEntity(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        published = published,
        content = content,
        isLiked = isLiked,
        shares = shares,
        views = views,
        likes = likes,
        video = video,
        visible = visible
    )
}

fun List<PostEntity>.toDTO() = map(PostEntity::toDTO)
fun List<Post>.fromDto() = map { PostEntity.Companion.fromDTO(it, visible = true) }