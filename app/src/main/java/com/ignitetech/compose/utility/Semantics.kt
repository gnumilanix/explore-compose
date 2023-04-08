package com.ignitetech.compose.utility

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import com.ignitetech.compose.ui.Screens

val DrawableId = SemanticsPropertyKey<List<Int>>(
    name = "DrawableResId",
    mergePolicy = mergeSemantics()
)

var SemanticsPropertyReceiver.drawableId: Int
    get() = throwSemanticsGetNotSupported()
    set(value) = set(DrawableId, listOf(value))

val DrawableUrl = SemanticsPropertyKey<List<String>>(
    name = "DrawableUrl",
    mergePolicy = mergeSemantics()
)

var SemanticsPropertyReceiver.drawableUrl: String?
    get() = throwSemanticsGetNotSupported()
    set(value) = set(DrawableUrl, listOf(value ?: ""))

val DrawableVector = SemanticsPropertyKey<List<ImageVector>>(
    name = "DrawableVector",
    mergePolicy = mergeSemantics()
)

var SemanticsPropertyReceiver.drawableVector: ImageVector
    get() = throwSemanticsGetNotSupported()
    set(value) = set(DrawableVector, listOf(value))

val Screen = SemanticsPropertyKey<List<Screens>>(
    name = "Screen",
    mergePolicy = mergeSemantics()
)

var SemanticsPropertyReceiver.screen: Screens
    get() = throwSemanticsGetNotSupported()
    set(value) = set(Screen, listOf(value))

private fun <T> mergeSemantics(): (List<T>?, List<T>) -> List<T> {
    return { parentValue, childValue ->
        parentValue?.toMutableList()?.also { it.addAll(childValue) } ?: childValue
    }
}

private fun <T> throwSemanticsGetNotSupported(): T {
    throw UnsupportedOperationException(
        "You cannot retrieve a semantics property directly - " +
            "use one of the SemanticsConfiguration.getOr* methods instead"
    )
}
