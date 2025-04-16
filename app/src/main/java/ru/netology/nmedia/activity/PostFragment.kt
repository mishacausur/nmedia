package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.Counter
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnActionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostViewModel
import ru.netology.nmedia.utils.LongArgs

class PostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

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

        val postId = requireArguments().longArgs
        val post = viewModel.data.value?.find { it.id == postId }

        post?.let { viewHolder.bind(it) }

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

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val _post = posts.find { it.id == postId } ?: return@observe
                with(binding.post) {
                likes.apply {
                    text = Counter.localizeCount(_post.likes)
                }
                share.apply {
                    text = Counter.localizeCount(_post.shares)
                }
                viewsCount.text = Counter.localizeCount(_post.views)
            }

        }
        return binding.root
    }

    companion object {
        var Bundle.longArgs by LongArgs
    }
}