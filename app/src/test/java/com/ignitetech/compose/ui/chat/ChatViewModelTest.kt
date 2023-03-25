package com.ignitetech.compose.ui.chat

import com.ignitetech.compose.data.chat.ChatRepository
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.data.user.UserRepository
import com.ignitetech.compose.domain.FormatInstantUseCase
import com.ignitetech.compose.domain.FormatLocalDateUseCase
import com.ignitetech.compose.domain.InstantToLocalDateUseCase
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var chatRepository: ChatRepository

    @MockK
    lateinit var preferenceRepository: PreferenceRepository

    @MockK
    lateinit var formatInstantUseCase: FormatInstantUseCase

    @MockK
    lateinit var formatLocalDateUseCase: FormatLocalDateUseCase

    @MockK
    lateinit var instantToLocalDateUseCase: InstantToLocalDateUseCase

    lateinit var viewModel: ChatViewModel
}