package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content=:text WHERE id=:postId")
    fun updatePost(postId: Long, text: String)

    @Query(
        """
           UPDATE PostEntity SET
               likes = likes + CASE WHEN isLiked THEN -1 ELSE 1 END,
               isLiked = CASE WHEN isLiked THEN 0 ELSE 1 END
           WHERE id =:postId;
        """
    )
    fun like(postId: Long)

    @Query(
        """
           UPDATE PostEntity SET
               shares = shares + 1
           WHERE id =:postId;
        """
    )
    fun share(postId: Long)

    @Query("DELETE FROM PostEntity WHERE id=:postId")
    fun remove(postId: Long)

    fun save(post: PostEntity) = if (post.id == 0L) insert(post) else updatePost(post.id, post.content)
}