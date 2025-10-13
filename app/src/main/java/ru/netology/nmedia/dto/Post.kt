package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName
import java.net.URL

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val authorId: Long,
    val published: String,
    val content: String,
    @SerializedName("likedByMe")
    val isLiked: Boolean = false,
    val shares: UInt = 0u,
    val views: UInt = 0u,
    val likes: Int = 0,
    val video: URL? = null,
    val visible: Boolean = true,
    val ownedByMe: Boolean = false
)
