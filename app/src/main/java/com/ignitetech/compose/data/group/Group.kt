package com.ignitetech.compose.data.group

import com.ignitetech.compose.data.user.User

data class Group(val name: String, val users: List<User>)