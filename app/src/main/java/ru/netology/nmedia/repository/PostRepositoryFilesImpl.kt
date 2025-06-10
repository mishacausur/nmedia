//package ru.netology.nmedia.repository
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import ru.netology.nmedia.dto.Post
//
//class PostRepositoryFilesImpl(private val context: Context) : PostRepository {
//
//    private var nextId: Long = 1
//    private var posts = emptyList<Post>()
//        set(value) {
//            field = value
//            data.value = value
//            sync()
//        }
//
//    private val data = MutableLiveData(posts)
//
//    init {
//        val file = context.filesDir.resolve(FILE_NAME)
//        if (file.exists()) {
//            context.openFileInput(FILE_NAME).bufferedReader().use {
//                posts = gson.fromJson(it, type)
//                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
//            }
//        } else {
//            sync()
//        }
//    }
//
//    override fun getAll(): LiveData<List<Post>> = data
//
//    override fun like(postId: Long) {
//        posts = posts.map {
//            if (it.id != postId) it else it.copy(
//                isLiked = !it.isLiked,
//                likes = if (!it.isLiked) it.likes + 1u else it.likes - 1u
//            )
//        }
//    }
//
//    override fun share(postId: Long) {
//        posts = posts.map {
//            if (it.id != postId) it else it.copy(shares = it.shares + 1u)
//        }
//    }
//
//    override fun remove(postId: Long) {
//        posts = posts.filter { it.id != postId }
//    }
//
//    override fun save(post: Post) {
//        posts = if (post.id == 0.toLong()) {
//            listOf(post.copy(id = nextId++, author = "Me", published = "now")) + posts
//        } else {
//            posts.map {
//                if (it.id != post.id) it else it.copy(content = post.content)
//            }
//        }
//    }
//
//    fun sync() {
//        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
//            it.write(gson.toJson(posts))
//        }
//    }
//
//    companion object {
//        private const val FILE_NAME = "posts.json"
//        private val gson = Gson()
//        private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
//    }
//}