package com.ignitetech.compose.data.call

import com.ignitetech.compose.data.user.User
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor() {
    fun getCalls(): List<Call> {
        return listOf(
            Call(
                0, 1, 1000, Type.INCOMING,
                Calendar.getInstance(),
                User(1, "John", "http://placekitten.com/200/300")
            ),
            Call(
                0, 1, 60000, Type.OUTGOING,
                Calendar.getInstance(),
                User(2, "Jane", "http://placekitten.com/200/100")
            ), Call(
                0, 1, 0, Type.INCOMING_MISSED,
                Calendar.getInstance(),
                User(1, "John", "http://placekitten.com/200/300")
            ),
            Call(
                0, 1, 0, Type.OUTGOING_MISSED,
                Calendar.getInstance(),
                User(2, "Jane", "http://placekitten.com/200/100")
            )
        )
    }
}