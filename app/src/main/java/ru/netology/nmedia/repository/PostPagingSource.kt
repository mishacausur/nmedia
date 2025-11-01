package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostPagingSource(
    private val apiService: PostApi
) : PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> apiService.getLatest(params.loadSize)

                is LoadParams.Append -> apiService.getBefore(id = params.key, count = params.loadSize)

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val data = response.body().orEmpty()
            return LoadResult.Page(
                data,
                prevKey = params.key,
                data.lastOrNull()?.id)

        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}