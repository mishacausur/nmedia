package ru.netology.nmedia.dto

data class Post(
    val id: UInt,
    val author: String,
    val published: String,
    var content: String,
    val isLiked: Boolean,
    val shares: UInt,
    val views: UInt,
    val likes: UInt,
)
