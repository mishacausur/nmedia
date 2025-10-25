package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PostFragment.Companion.longArgs
import ru.netology.nmedia.adapter.OnActionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.repository.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
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
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        binding.list.adapter = adapter
        val newPostsButton = binding.newPostsButton
        var shouldScrollToTop = false
        newPostsButton.setOnClickListener {
            viewModel.showPendingPosts()
            shouldScrollToTop = true
            newPostsButton.isVisible = false
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadPosts()
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        viewModel.newerCount.observe(viewLifecycleOwner) { count ->
            newPostsButton.isVisible = count > 0
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data.posts) {
                binding.emptyText.isVisible = data.empty
                if (shouldScrollToTop) {
                    binding.list.scrollToPosition(0)
                    shouldScrollToTop = false
                }
            }
            viewModel.state.observe(viewLifecycleOwner) { state ->
                binding.progress.isVisible = state.loading
                binding.errorGroup.isVisible = state.error
            }
            binding.swipeRefresh.isRefreshing = false
        }
        binding.swipeRefresh.isRefreshing = true

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

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.addButton)
                    .show()
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
        }

        return binding.root
    }

    private fun openVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}