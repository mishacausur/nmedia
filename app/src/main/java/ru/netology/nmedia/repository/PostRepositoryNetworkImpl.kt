package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit
import java.io.IOException

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

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun like(postId: Long, callback: PostRepository.GetAllCallback<Post>) {

        val allPosts = getAll()
        val post = allPosts.find { it.id == postId } ?: throw RuntimeException("Post not found")

        val requestBuilder = Request.Builder()
            .url("$BASE_URL/api/slow/posts/$postId/likes")

        val request = if (!post.isLiked) {
            requestBuilder.post("".toRequestBody()).build()
        } else {
            requestBuilder.delete().build()
        }
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError(IOException("Ошибка обновления поста: ${response.message}"))
                    return
                }

                val responseBody = response.body?.string() ?: run {
                    callback.onError(IOException("budy is null"))
                    return
                }

                val postResponse = gson.fromJson(responseBody, Post::class.java)
                callback.onSuccess(postResponse)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
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

    override fun remove(postId: Long, callback: PostRepository.GetAllCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$postId")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val postsString =
                        response.body?.string() ?: throw RuntimeException("body is null")

                    val savedPost = gson.fromJson(postsString, Post::class.java)
                    callback.onSuccess(savedPost)
                } else {
                    callback.onError(Exception("Ошибка при сохранении поста"))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }

}