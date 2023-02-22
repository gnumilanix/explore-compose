package com.ignitetech.compose.data.user

import com.ignitetech.compose.data.preference.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val preferenceRepository: PreferenceRepository
) {
    fun getMe(): Flow<User?> {
        return flow {
            emit(
                when (val userId = preferenceRepository.userId()) {
                    null -> null
                    else -> userDao.getUser(userId)
                }
            )
        }
    }

    fun getUsers(): Flow<List<User>> {
        return userDao.getAll()
    }

    fun getUsers(vararg ids: Int): Flow<List<User>> {
        return userDao.getUsers(*ids)
    }

    fun getUser(id: Int): Flow<User> {
        return userDao.getUserFlow(id)
    }
}