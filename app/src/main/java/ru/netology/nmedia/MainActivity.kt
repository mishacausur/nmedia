package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.repository.PostViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likes.setImageResource(
                    if (post.isLiked) {
                        R.drawable.ic_liked_24
                    } else {
                        R.drawable.ic_like_24
                    }
                )
                views.setImageResource(R.drawable.ic_eye)
                share.setImageResource(R.drawable.ic_share)
            }
        }

        viewModel.likes.observe(this) {
            with(binding) {
                likesCount.text = Counter.localizeCount(it)
            }
        }
        viewModel.shares.observe(this) {
            with(binding) {
                shareCount.text = Counter.localizeCount(it)
            }
        }

        viewModel.views.observe(this) {
            with(binding) {
                viewsCount.text = Counter.localizeCount(it)
            }
        }
        binding.likes.setOnClickListener {
            viewModel.like()
        }

        binding.share.setOnClickListener {
            viewModel.share()
        }
    }
}