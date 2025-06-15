package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryNetworkImpl: PostRepository  {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun like(postId: Long) {

        val allPosts = getAll()
        val post = allPosts.find { it.id == postId } ?: throw RuntimeException("Post not found")

        val requestBuilder = Request.Builder()
            .url("$BASE_URL/api/slow/posts/$postId/likes")

        val request = if (!post.isLiked) {
            requestBuilder.post("".toRequestBody()).build()
        } else {
            requestBuilder.delete().build()
        }
        println("Sending ${if (!post.isLiked) "POST" else "DELETE"} for post $postId")
        client.newCall(request)
            .execute()
            .close()
    }

    override fun share(postId: Long) {

        println("""
            val request: Request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$postId/shares")
            .build()

        client.newCall(request)
            .execute()
            .close()
        """.trimIndent())

    }

    override fun remove(postId: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$postId")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}