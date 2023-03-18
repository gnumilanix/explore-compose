package com.ignitetech.compose.data.chat

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM Chat")
    fun getAll(): Flow<List<Chat>>

    @Transaction
    @Query("SELECT * FROM Chat WHERE id IN (SELECT MAX(id) FROM Chat GROUP BY user_id) ORDER BY date DESC")
    fun getLatestChatsWithSender(): Flow<List<ChatWithSender>>

    @Transaction
    @Query("SELECT * FROM Chat WHERE user_id = :senderId ORDER BY date ASC")
    fun getChatsWithSender(senderId: Int): Flow<List<ChatWithSender>>

    @Insert
    suspend fun saveChats(vararg chats: Chat)

    @Delete
    suspend fun deleteChats(vararg chats: Chat)
}
