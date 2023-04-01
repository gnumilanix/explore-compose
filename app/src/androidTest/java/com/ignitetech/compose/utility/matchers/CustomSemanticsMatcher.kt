package com.ignitetech.compose.utility.matchers

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import com.ignitetech.compose.ui.Screens
import com.ignitetech.compose.utility.DrawableId
import com.ignitetech.compose.utility.DrawableUrl
import com.ignitetech.compose.utility.DrawableVector
import com.ignitetech.compose.utility.Screen

fun hasDrawable(@DrawableRes id: Int) = matcher(DrawableId, id)

fun hasDrawable(url: String?) = matcher(DrawableUrl, url)

fun hasDrawable(vector: ImageVector) = matcher(DrawableVector, vector)

fun hasDrawables(vararg urls: String) = allMatcher(DrawableUrl, *urls)

private fun <T> matcher(property: SemanticsPropertyKey<List<T>>, expected: T?): SemanticsMatcher {
    return SemanticsMatcher("${property.name} = [$expected]") {
        it.config.getOrNull(property)?.any { item -> item == expected } ?: false
    }
}

private fun <T> allMatcher(
    property: SemanticsPropertyKey<List<T>>,
    vararg values: T
): SemanticsMatcher {
    return SemanticsMatcher("${property.name} = [${values.joinToString(", ")}]") {
        val expectedValues = values.toList()
        val actualValues: List<Any?> = it.config.getOrNull(property) ?: listOf<T>()
        actualValues.containsAll(expectedValues) && expectedValues.containsAll(actualValues)
    }
}

fun hasScreen(screen: Screens) = matcher(Screen, screen)

fun withRole(role: Role) = SemanticsMatcher("${SemanticsProperties.Role.name} contains '$role'") {
    val roleProperty = it.config.getOrNull(SemanticsProperties.Role) ?: false
    roleProperty == role
}
