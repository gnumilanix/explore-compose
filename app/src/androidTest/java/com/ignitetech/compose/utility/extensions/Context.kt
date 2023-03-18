package com.ignitetech.compose.utility.extensions

import android.content.Context
import java.io.File

fun Context.deleteDatastore() {
    File(filesDir, "datastore").deleteRecursively()
}
