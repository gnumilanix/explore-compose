package com.ignitetech.compose.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.ui.Screens.HomeScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val onboardComplete = preferenceRepository.onboardCompleteFlow
    private var tabs by mutableStateOf(defaultTabs)

    val state = combine(
        onboardComplete,
        snapshotFlow { tabs }
    ) { onboardComplete, tabs ->
        HomeUiState(onboardComplete, tabs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = HomeUiState()
    )

    fun onboardComplete() {
        viewModelScope.launch {
            preferenceRepository.userId(0) //TODO Temporary hardcoded value
            preferenceRepository.onboardComplete(true)
        }
    }
}

private val defaultTabs = listOf(
    HomeScreens.Chats,
    HomeScreens.Groups,
    HomeScreens.Calls
)

data class HomeUiState(
    val onboardComplete: Boolean? = null,
    val tabs: List<HomeScreens> = defaultTabs
)