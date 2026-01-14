package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val appDb: AppDb,
    private val apiService: PostApi,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {

        return  try {
            when (loadType) {
                LoadType.REFRESH -> handleRefresh(state.config.pageSize)
                LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> handleAppend(state.config.pageSize)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun handleRefresh(pageSize: Int): MediatorResult {
        val latestId = postDao.getLatestId()
        val response = if (latestId == null) {
            apiService.getLatest(pageSize)
        } else {
            apiService.getAfter(latestId, pageSize)
        }
        if (!response.isSuccessful) throw HttpException(response)
        val data = response.body().orEmpty()
        if (data.isNotEmpty()) {
            val entities = data.map { PostEntity.fromDTO(it) }
            appDb.withTransaction {
                postDao.insert(entities)
                updateRemoteKeys()
            }
        } else if (latestId == null) {
            postRemoteKeyDao.clear()
        }
        return MediatorResult.Success(endOfPaginationReached = data.isEmpty())
    }

    private suspend fun handleAppend(pageSize: Int): MediatorResult {
        val oldestId = postDao.getOldestId()
            ?: return MediatorResult.Success(endOfPaginationReached = true)

        val response = apiService.getBefore(oldestId, pageSize)
        if (!response.isSuccessful) throw HttpException(response)
        val data = response.body().orEmpty()

        if (data.isNotEmpty()) {
            val entities = data.map { PostEntity.fromDTO(it) }
            appDb.withTransaction {
                postDao.insert(entities)
                updateRemoteKeys()

            }
        }

        return MediatorResult.Success(endOfPaginationReached = data.isEmpty())
    }

    private suspend fun updateRemoteKeys() {
        val latestId = postDao.getLatestId()
        val oldestId = postDao.getOldestId()

        if (latestId == null || oldestId == null) {
            postRemoteKeyDao.clear()
            return
        }

        postRemoteKeyDao.insert(
            listOf(
                PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, latestId),
                PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, oldestId)
            )
        )
    }
}