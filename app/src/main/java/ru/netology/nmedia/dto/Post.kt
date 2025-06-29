package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName
import java.net.URL

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String?,
    val published: String,
    var content: String,
    @SerializedName("likedByMe")
    val isLiked: Boolean,
    val shares: UInt,
    val views: UInt,
    val likes: Int,
    val video: URL? = null
)
