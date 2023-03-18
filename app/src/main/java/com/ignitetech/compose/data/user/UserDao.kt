package com.ignitetech.compose.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUser(id: Int): User

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserFlow(id: Int): Flow<User>

    @Query("SELECT * FROM User WHERE id IN (:ids)")
    fun getUsers(vararg ids: Int): Flow<List<User>>

    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>

    @Insert
    suspend fun saveUsers(vararg users: User)

    @Query("INSERT INTO User VALUES(:id, :name, :avatar)")
    suspend fun saveUser(id: Int, name: String, avatar: String?)
}
