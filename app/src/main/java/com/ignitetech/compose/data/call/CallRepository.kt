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

    suspend fun saveCall(vararg calls: Call) {
        callDao.saveCalls(*calls)
    }

    suspend fun deleteCall(vararg calls: Call) {
        callDao.deleteCalls(*calls)
    }
}
