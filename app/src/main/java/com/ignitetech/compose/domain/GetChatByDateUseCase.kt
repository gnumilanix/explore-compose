package com.ignitetech.compose.domain

import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.chat.ChatWithSender
import com.ignitetech.compose.data.chat.Direction
import com.ignitetech.compose.data.user.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatByDateUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val formatInstantUseCase: FormatInstantUseCase,
    private val formatLocalDateUseCase: FormatLocalDateUseCase,
    private val instantToLocalDateUseCase: InstantToLocalDateUseCase,
) {
    operator fun invoke(recipientId: Int): Flow<List<ChatsByDate>> {
        return chatRepository.getChats(recipientId).map { chats ->
            chats.map { instantToLocalDateUseCase(it.chat.date) to it }
                .groupBy { it.first }
                .map { it.key to it.value.map { callMap -> callMap.second } }
                .map {
                    ChatsByDate(
                        formatLocalDateUseCase("MMMM dd", it.first),
                        it.second.map { chat -> chat.asDetail(formatInstantUseCase) }
                    )
                }
        }
    }
}

fun ChatWithSender.asDetail(
    formatInstantUseCase: FormatInstantUseCase
) = ChatDetail(
    chat.id,
    chat.userId,
    chat.message,
    chat.direction,
    formatInstantUseCase("MM/dd", chat.date),
    sender
)

data class ChatDetail(
    val id: Int,
    val userId: Int,
    val message: String,
    val direction: Direction,
    val date: String,
    val sender: User? = null
)

data class ChatsByDate(
    val date: String,
    val calls: List<ChatDetail>
)