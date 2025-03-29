package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.Counter
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

typealias OnLikeListener = (Post) -> Unit

class PostAdapter(private val onLikeListener: OnLikeListener) : RecyclerView.Adapter<PostViewHolder>() {

    var list: List<Post> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, onLikeListener)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener
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
            onLikeListener(post)
        }
        views.setImageResource(R.drawable.ic_eye)
        share.setImageResource(R.drawable.ic_share)
    }


}