package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Counter
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

enum class PostAction {
    like, share, remove
}

data class Action(
    val action: PostAction,
    val post: Post
)
typealias OnActionListener = (Action) -> Unit

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
    fun bind(post: Post) = with(binding) {
        author.text = post.author
        published.text = post.published
        content.text = post.content
        likesCount.text = Counter.localizeCount(post.likes)
        shareCount.text = Counter.localizeCount(post.shares)
        viewsCount.text = Counter.localizeCount(post.views)
        likes.setImageResource(
            if (post.isLiked) {
                R.drawable.ic_liked_24
            } else {
                R.drawable.ic_like_24
            }
        )
        likes.setOnClickListener {
            onActionListener(Action(PostAction.like, post))
        }
        share.setOnClickListener {
            onActionListener(Action(PostAction.share, post))
        }
        views.setImageResource(R.drawable.ic_eye)
        share.setImageResource(R.drawable.ic_share)
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_actions)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onActionListener(Action(PostAction.remove, post))
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