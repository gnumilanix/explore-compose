package com.ignitetech.compose.data.seed

import android.content.Context
import android.content.res.AssetManager
import androidx.work.Data
import androidx.work.ListenableWorker.Result.Failure
import androidx.work.ListenableWorker.Result.Success
import androidx.work.WorkerParameters
import com.ignitetech.compose.data.call.CallDao
import com.ignitetech.compose.data.chat.ChatDao
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.rules.TestDispatcherRule
import com.ignitetech.compose.test.utility.InstantWorkTaskExecutor
import com.ignitetech.compose.test.utility.getResourceAsStream
import com.ignitetech.compose.utility.Constants
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)
class DatabaseSeedWorkerTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var userDao: UserDao

    @MockK
    lateinit var chatDao: ChatDao

    @MockK
    lateinit var callDao: CallDao

    lateinit var worker: DatabaseSeedWorker

    @Before
    fun setUp() {
        val testExecutor = InstantWorkTaskExecutor()
        val workerParameters = WorkerParameters(
            UUID.randomUUID(),
            Data.EMPTY,
            emptyList(),
            mockk(),
            1,
            1,
            testExecutor.serialTaskExecutor,
            testExecutor,
            mockk(),
            mockk(),
            mockk()
        )
        worker = DatabaseSeedWorker(context, workerParameters, userDao, chatDao, callDao)
    }

    @Test
    fun `doWork returns Failure on error`() = runTest {
        every { context.applicationContext } throws NullPointerException()

        assertTrue(worker.doWork() is Failure)
    }

    @Test
    fun `doWork returns performs work correctly`() = runTest {
        val assets = mockk<AssetManager>()
        val data = Json.decodeFromStream<Seed>(getResourceAsStream("compose-db.json"))

        every { context.assets } returns assets
        every { assets.open(any()) } returns getResourceAsStream("compose-db.json")
        coJustRun { userDao.saveUser(any(), any(), any()) }
        coJustRun { chatDao.saveChats(*anyVararg()) }
        coJustRun { callDao.saveCalls(*anyVararg()) }

        assertTrue(worker.doWork() is Success)

        verify { assets.open(Constants.DATABASE_SEED_FILE) }
        coVerify { chatDao.saveChats(*data.chats.toTypedArray()) }
        coVerify { callDao.saveCalls(*data.calls.toTypedArray()) }
        data.users.forEach { user ->
            coVerify { userDao.saveUser(user.id, user.name, user.avatar) }
        }
    }
}