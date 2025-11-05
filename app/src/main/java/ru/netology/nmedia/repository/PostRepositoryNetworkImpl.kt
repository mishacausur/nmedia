package ru.netology.nmedia.repository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.fromDto
import ru.netology.nmedia.entity.toDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryNetworkImpl @Inject constructor(
    private val dao: PostDao,
    private val postApi: PostApi
) : PostRepository {

    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(
                apiService = postApi
            )
        }
    ).flow

    override fun newerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000)
            val reponse = postApi.getNewer(id)
            if (!reponse.isSuccessful) {
                throw HttpException(reponse.code())
            }
            val body = reponse.body() ?: throw  HttpException(reponse.code())
            body.fromDto().forEach {
                dao.insert(it.copy(visible = false))
            }
            emit(body.size)
        }
    }.catch {
        throw it
    }

    override suspend fun setAllVisible() {
        dao.setAllVisible()
    }

    override fun getNewerLocalCount(): Flow<Int> = dao.getNewerCount()


    override suspend fun like(postId: Long) {
        val isLiked = dao.getById(postId)?.isLiked ?: return
        dao.like(postId)
        try {
            if (!isLiked) {
                postApi.like(postId)
            } else {
                postApi.unlike(postId)
            }
        } catch (e: Exception) {
            dao.like(postId)
            throw e
        }
    }

    override suspend fun share(postId: Long) {
        try {
            println(
                """
                val request: Request = Request.Builder()
                .post("".toRequestBody())
                .url("BASE_URL/api/slow/posts/$postId/shares")
                .build()

            client.newCall(request)
                .execute()
                .close()
            """.trimIndent()
            )
            dao.share(postId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun removeLocally(postId: Long) {
        dao.remove(postId)
    }

    override suspend fun remove(id: Long) {
        removeLocally(id)
        try {
            postApi.remove(id)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun save(post: Post): Post {
        try {
            val response = postApi.save(post)
            dao.insert(PostEntity.fromDTO(response))
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllAsync() {
        try {
            val posts = postApi.getAll()
            posts.fromDto().forEach {
                dao.insert(it.copy(visible = true))
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun undoLike(postId: Long) {
        val post = dao.getById(postId) ?: return
        val liked = !post.isLiked
        dao.insert(post.copy(
            isLiked = liked,
            likes = if (liked) post.likes + 1 else post.likes - 1
        ))
    }

    override suspend fun getPostById(postId: Long): Post? {
        return dao.getById(postId)?.toDTO()
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}