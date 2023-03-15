package com.ignitetech.compose.test.utility

import com.ignitetech.compose.rules.TestDispatcherRule
import java.io.InputStream
import kotlin.reflect.KClass

fun getResourceAsStream(
    filename: String,
    clazz: KClass<*> = TestDispatcherRule::class
): InputStream {
    return clazz.java.classLoader!!.getResourceAsStream(filename)
}