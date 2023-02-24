package com.ignitetech.compose.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.preference.PreferenceRepository
import com.ignitetech.compose.ui.Screens.HomeScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    val onboardComplete = preferenceRepository.onboardCompleteFlow

    private val _tabs = MutableStateFlow(listOf<HomeScreens>())
    val tabs = _tabs.asStateFlow()

    init {
        _tabs.update {
            listOf(HomeScreens.Chats, HomeScreens.Groups, HomeScreens.Calls)
        }
    }

    fun onboardComplete() {
        viewModelScope.launch {
            preferenceRepository.onboardComplete(true)
        }
    }
}