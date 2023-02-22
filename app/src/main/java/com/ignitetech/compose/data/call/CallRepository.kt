package com.ignitetech.compose.data.call

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val callDao: CallDao
) {
    fun getCalls(): Flow<List<CallWithTarget>> {
        return callDao.getCallsWithTarget()
    }

    suspend fun saveCall(call: Call) {
        return callDao.saveCalls(call)
    }

    suspend fun deleteCall(vararg calls: Call) {
        return callDao.deleteCalls(*calls)
    }
}