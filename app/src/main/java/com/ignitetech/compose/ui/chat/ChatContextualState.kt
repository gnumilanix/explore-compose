package com.ignitetech.compose.ui.chat

import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateMap

@Stable
class ChatContextualState(
    initialSelectedItems: Map<Int, Boolean> = mapOf()
) {
    private var _selectedItems = initialSelectedItems.entries
        .map { it.key to it.value }
        .toMutableStateMap()

    var selectedItems: Map<Int, Boolean> = mapOf()
        get() = _selectedItems
        private set

    var inSelectionMode: Boolean = false
        get() = selectedItems.containsValue(true)
        private set

    fun clearSelection() {
        _selectedItems.clear()
    }

    fun selected(id: Int, selected: Boolean) {
        _selectedItems[id] = selected
    }

    fun isSelected(id: Int): Boolean {
        return _selectedItems.getOrElse(id) { false }
    }
}
