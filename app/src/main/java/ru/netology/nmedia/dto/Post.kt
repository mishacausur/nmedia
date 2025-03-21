package ru.netology.nmedia.dto

data class Post(
    val id: UInt,
    val author: String,
    val published: String,
    val content: String,
    var isLiked: Boolean,
)
