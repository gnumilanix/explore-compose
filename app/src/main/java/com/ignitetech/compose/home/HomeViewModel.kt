package com.ignitetech.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.data.preference.PreferenceRepository
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

    private val _tabs = MutableStateFlow(listOf<HomeTabs>())
    val tabs = _tabs.asStateFlow()

    init {
        _tabs.update {
            listOf(HomeTabs.Chat, HomeTabs.Group, HomeTabs.Call)
        }
    }

    fun onboardComplete() {
        viewModelScope.launch {
            preferenceRepository.onboardComplete(true)
        }
    }
}