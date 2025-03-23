package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun getLikes(): LiveData<UInt>
    fun getShares(): LiveData<UInt>
    fun getViews(): LiveData<UInt>
    fun like()
    fun share()
}