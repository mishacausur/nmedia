package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET content=:text WHERE id=:postId")
    suspend fun updatePost(postId: Long, text: String)

    @Query(
        """
           UPDATE PostEntity SET
               likes = likes + CASE WHEN isLiked THEN -1 ELSE 1 END,
               isLiked = CASE WHEN isLiked THEN 0 ELSE 1 END
           WHERE id =:postId;
        """
    )
    suspend fun like(postId: Long)

    @Query(
        """
           UPDATE PostEntity SET
               shares = shares + 1
           WHERE id =:postId;
        """
    )
    suspend fun share(postId: Long)

    @Query("DELETE FROM PostEntity WHERE id=:postId")
    suspend fun remove(postId: Long)

    suspend fun save(post: PostEntity) = if (post.id == 0L) insert(post) else updatePost(post.id, post.content)

    @Query("SELECT * FROM PostEntity WHERE id = :postId LIMIT 1")
    suspend fun getById(postId: Long): PostEntity?
}