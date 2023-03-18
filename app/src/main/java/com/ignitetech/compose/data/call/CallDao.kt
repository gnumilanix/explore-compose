package com.ignitetech.compose.data.call

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Query("SELECT * FROM Call ORDER BY date DESC")
    fun getAll(): Flow<List<Call>>

    @Transaction
    @Query("SELECT * FROM Call ORDER BY date DESC")
    fun getCallsWithTarget(): Flow<List<CallWithTarget>>

    @Insert
    suspend fun saveCalls(vararg calls: Call)

    @Delete
    suspend fun deleteCalls(vararg calls: Call)
}
