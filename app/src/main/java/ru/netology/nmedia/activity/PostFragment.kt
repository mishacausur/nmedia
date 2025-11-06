package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.netology.nmedia.Counter
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnActionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostViewModel
import ru.netology.nmedia.utils.LongArgs

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )

        val postCardBinding = binding.post
        val postId = requireArguments().longArgs

        val viewHolder = PostViewHolder(
            postCardBinding,
            object : OnActionListener {
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
                    findNavController().navigateUp()
                }

                override fun onPlay(post: Post) {
                    //openVideo(post.video.toString())
                }

                override fun onOpenPost(postId: Long) = Unit
            })

        lifecycleScope.launch {
            val post = viewModel.getPostById(postId)
            post?.let { p ->
                viewHolder.bind(p)
                with(binding.post) {
                    likes.apply {
                        text = Counter.localizeCount(p.likes.toUInt())
                        isChecked = p.isLiked
                    }
                    share.apply {
                        text = Counter.localizeCount(p.shares)
                    }
                    viewsCount.text = Counter.localizeCount(p.views)
                }
            }
        }
        

        lifecycleScope.launch {
            viewModel.data.collect {
                lifecycleScope.launch {
                    val post = viewModel.getPostById(postId)
                    post?.let { p ->
                        with(binding.post) {
                            likes.apply {
                                text = Counter.localizeCount(p.likes.toUInt())
                                isChecked = p.isLiked
                            }
                            share.apply {
                                text = Counter.localizeCount(p.shares)
                            }
                            viewsCount.text = Counter.localizeCount(p.views)
                        }
                    }
                }
            }
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (!it.content.isBlank()) {
                findNavController().navigate(
                    R.id.action_postFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = it.content
                    }
                )
            }
        }

        return binding.root
    }

    companion object {
        var Bundle.longArgs by LongArgs
    }
}