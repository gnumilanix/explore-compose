package com.ignitetech.compose.ui.call

import com.ignitetech.compose.data.call.CallRepository
import com.ignitetech.compose.domain.FormatInstantUseCase
import com.ignitetech.compose.domain.FormatLocalDateUseCase
import com.ignitetech.compose.domain.InstantToLocalDateUseCase
import com.ignitetech.compose.rules.TestDispatcherRule
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class CallViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var formatInstantUseCase: FormatInstantUseCase

    @MockK
    lateinit var formatLocalDateUseCase: FormatLocalDateUseCase

    @MockK
    lateinit var instantToLocalDateUseCase: InstantToLocalDateUseCase

    @InjectMockKs
    lateinit var viewModel: CallViewModel

}