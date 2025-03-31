package ru.netology.nmedia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.OnActionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostViewModel
import ru.netology.nmedia.utils.AndroidUtils

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
    private val adapter = PostAdapter(object : OnActionListener {
        override fun onLike(post: Post) {
            viewModel.like(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.share(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onRemove(post: Post) {
            viewModel.remove(post.id)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.list.adapter = adapter
        viewModel.edited.observe(this) {
            if (it.id != 0u) {
                binding.content.setText(it.content)
                binding.content.requestFocus()
            }
        }
        viewModel.data.observe(this) { posts ->
            val isNewPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (isNewPost) {
                    binding.list.scrollToPosition(0)
                }

            }
        }
        with(binding) {
            addButton.setOnClickListener {
                val text = content.text.toString()
                if (text.isBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.empty_text,
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.save(text)
                content.setText("")
                content.clearFocus()
                AndroidUtils.hideKeyboard(content)
            }
        }

    }
}