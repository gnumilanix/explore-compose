package com.ignitetech.compose.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _tabs = MutableStateFlow(listOf<HomeTabs>())
    val tabs = _tabs.asStateFlow()

    init {
        _tabs.update {
            listOf(HomeTabs.Chat, HomeTabs.Group, HomeTabs.Call)
        }
    }
}