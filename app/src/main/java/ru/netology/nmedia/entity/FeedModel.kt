package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState(
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
)