package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post

class PostRepositoryNetworkImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {

        return ApiService.service.getAll().enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>, response: Response<List<Post>>
                ) {
                    val body = response.body() ?: run {
                        callback.onError(RuntimeException("body is null"))
                        println("Ошибка загрузки: body is null")
                        return
                    }
                    callback.onSuccess(body)

                }

                override fun onFailure(
                    call: Call<List<Post>>, t: Throwable
                ) {
                    callback.onError(t)
                    println("Ошибка загрузки: ${t.message}")
                }

            })
    }

    override fun like(postId: Long, isLiked: Boolean, callback: PostRepository.GetAllCallback<Post>) {
        val call = if (!isLiked) {
            ApiService.service.like(postId)
        } else {
            ApiService.service.unlike(postId)
        }
        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("body is null"))
                    println("Ошибка при лайке: body is null")
                    return
                }
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(t)
                println("Ошибка при лайке: ${t.message}")
            }
        })
    }

    override fun share(postId: Long) {

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

    }

    override fun remove(postId: Long, callback: PostRepository.GetAllCallback<Unit>) {
        ApiService.service.remove(postId).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(t)
                    println("Ошибка при удалении: ${t.message}")
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        ApiService.service.save(post).enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: Call<Post>, response: Response<Post>
                ) {

                    val body = response.body() ?: run {
                        callback.onError(RuntimeException("body is null"))
                        println("Ошибка при сохранении: body is null")
                        return
                    }
                    callback.onSuccess(body)
                }

                override fun onFailure(
                    call: Call<Post>, t: Throwable
                ) {
                    callback.onError(t)
                    println("Ошибка при сохранении: ${t.message}")
                }
            })
    }

}