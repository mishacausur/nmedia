package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = AppAuth.getInstance().data.value?.let { token ->
            chain.request().newBuilder()
                .addHeader("Authorization", token.token)
                .build()
        } ?: chain.request()
        chain.proceed(request)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("http://10.0.2.2:9999/api/slow/")
    .build()

interface PostApi {

    @GET("posts")
    suspend fun getAll(): List<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun remove(@Path("id") id: Long)

    @POST("posts/{id}/likes")
    suspend fun like(@Path("id") id: Long): Post

    @DELETE("posts/{id}/likes")
    suspend fun unlike(@Path("id") id: Long): Post

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApi>()
    }
}