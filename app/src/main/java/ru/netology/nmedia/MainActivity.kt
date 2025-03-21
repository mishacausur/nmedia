package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var shared: UInt = 0u
        var viewed: UInt = 1_000_000u
        val post = Post(
            id = 1u,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            isLiked = false
        )

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            shareCount.text = shared.toString()
            viewsCount.text = Counter.localizeCount(viewed)
            likesCount.text = if (post.isLiked) "1" else "0"
            likes.setImageResource(R.drawable.ic_like_24)
            likes.setOnClickListener {
                post.isLiked = !post.isLiked
                    likes.setImageResource(
                        if (post.isLiked) {
                            R.drawable.ic_liked_24
                        } else {
                            R.drawable.ic_like_24
                        }
                    )
                likesCount.text = if (post.isLiked) "1" else "0"
            }
            share.setOnClickListener {
                shared++
                shareCount.text = shared.toString()
            }
            views.setImageResource(R.drawable.ic_eye)
            share.setImageResource(R.drawable.ic_share)
        }
    }
}