package com.ignitetech.compose.ui.settings

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignitetech.compose.R
import com.ignitetech.compose.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    private val _user = userRepository.getMe()
    private var _selectedAvatar by mutableStateOf<Bitmap?>(null)
    private var _message by mutableStateOf(R.string.lorem_ipsum)

    val state = combine(
        _user,
        snapshotFlow { _selectedAvatar },
        snapshotFlow { _message }
    ) { user, selectedAvatar, message ->
        SettingsUiState(selectedAvatar ?: user?.avatar, message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = SettingsUiState()
    )

    fun updateAvatar(image: Bitmap?) {
        _selectedAvatar = image
    }
}

data class SettingsUiState(
    val avatar: Any? = null,
    @StringRes val message: Int = R.string.lorem_ipsum
)
