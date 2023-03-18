package com.ignitetech.compose.data.seed

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ignitetech.compose.data.call.CallDao
import com.ignitetech.compose.data.chat.ChatDao
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.utility.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@HiltWorker
@OptIn(ExperimentalSerializationApi::class)
class DatabaseSeedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val callDao: CallDao
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            applicationContext.assets.open(Constants.DATABASE_SEED_FILE).use {
                Json.decodeFromStream<Seed>(it).apply {
                    withContext(Dispatchers.IO) {
                        users.onEach { user ->
                            userDao.saveUser(user.id, user.name, user.avatar)
                        }
                        chatDao.saveChats(*chats.toTypedArray())
                        callDao.saveCalls(*calls.toTypedArray())
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
