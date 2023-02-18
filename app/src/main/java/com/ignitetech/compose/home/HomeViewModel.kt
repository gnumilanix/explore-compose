package com.ignitetech.compose.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _tabs = MutableStateFlow(listOf<HomeTabs>())
    val tabs = _tabs.asStateFlow()

    init {
        _tabs.update {
            listOf(HomeTabs.Chat, HomeTabs.Group, HomeTabs.Call)
        }
    }
}