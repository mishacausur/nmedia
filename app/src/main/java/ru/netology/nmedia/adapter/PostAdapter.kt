package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.Counter
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.toURLOrNull

interface OnActionListener {
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onPlay(post: Post)
    fun onOpenPost(postId: Long)
}

class PostAdapter(private val onActionListener: OnActionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onActionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onActionListener: OnActionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        bindPostData(post)
        binding.root.setOnClickListener {
            onActionListener.onOpenPost(post.id)
        }
    }

    private fun bindPostData(post: Post) = with(binding) {
        author.text = post.author
        published.text = post.published
        content.text = post.content
        viewsCount.text = Counter.localizeCount(post.views)
        likes.apply {
            isChecked = post.isLiked
            text = Counter.localizeCount(post.likes.toUInt())
        }
        likes.setOnClickListener {
            onActionListener.onLike(post)
        }
        share.apply {
            text = Counter.localizeCount(post.shares)
        }
        share.setOnClickListener {
            onActionListener.onShare(post)
        }
        video.visibility = if (post.video == null) View.GONE else View.VISIBLE
        video.setOnClickListener {
            onActionListener.onPlay(post)
        }
        views.setImageResource(R.drawable.ic_eye)

        val avatarUrl = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
        Glide.with(binding.root)
            .load(avatarUrl)
            .error(R.drawable.ic_error_24)
            .placeholder(R.drawable.ic_avatar_placeholder_24)
            .timeout(10_000)
            .circleCrop()
            .into(avatar)


        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onActionListener.onRemove(post)
                            true
                        }

                        R.id.edit -> {
                            onActionListener.onEdit(post)
                            true
                        }

                        else -> false
                    }
                }
            }.show()
        }
    }
}

object PostDiffCallBack : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}