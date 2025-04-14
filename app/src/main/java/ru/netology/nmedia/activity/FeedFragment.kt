package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PostFragment.Companion.longArgs
import ru.netology.nmedia.adapter.OnActionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
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

        override fun onPlay(post: Post) {
            openVideo(post.video.toString())
        }

        override fun onOpenPost(postId: Long) {
            findNavController().navigate(
                R.id.action_feedFragment_to_postFragment,
                Bundle().apply {
                    longArgs = postId
                }
            )
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val isNewPost = adapter.currentList.size < posts.size
            adapter.submitList(posts) {
                if (isNewPost) {
                    binding.list.scrollToPosition(0)
                }
            }
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (!it.content.isBlank()) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = it.content
                    }
                )
            }
        }
        return binding.root
    }

    private fun openVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}