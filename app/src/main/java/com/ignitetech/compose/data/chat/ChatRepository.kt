package com.ignitetech.compose.data.chat

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {
    fun getLatestChats(): Flow<List<ChatWithSender>> {
        return chatDao.getLatestChatsWithSender()
    }

    fun getChats(senderId: Int): Flow<List<ChatWithSender>> {
        return chatDao.getChatsWithSender(senderId)
    }
}
