package com.ignitetech.compose.data.call

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.ignitetech.compose.data.AppDatabase
import com.ignitetech.compose.data.user.User
import com.ignitetech.compose.data.user.UserDao
import com.ignitetech.compose.utility.rules.TestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class CallDaoTest {
    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var callDao: CallDao
    private lateinit var userDao: UserDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        callDao = appDatabase.callDao()
        userDao = appDatabase.userDao()
    }

    @Test
    @Throws(IOException::class)
    fun getAllReturnsAll() = runTest {
        val now = Clock.System.now()
        val call1 = Call(1, 1, 100, Type.OUTGOING_MISSED, now)
        val call2 = Call(2, 2, 200, Type.INCOMING, now)

        callDao.saveCalls(call1, call2)
        callDao.getAll().test {
            awaitItem().also { calls ->
                assert(calls.size == 2)
                assert(calls.contains(call1))
                assert(calls.contains(call2))
                cancel()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun deleteCallsDeletesGivenCalls() = runTest {
        val now = Clock.System.now()
        val call1 = Call(1, 1, 100, Type.OUTGOING_MISSED, now)
        val call2 = Call(2, 2, 200, Type.INCOMING, now)

        callDao.saveCalls(call1, call2)
        callDao.deleteCalls(call1)
        callDao.getAll().test {
            awaitItem().also { calls ->
                assert(calls.size == 1)
                assert(calls.contains(call2))
                cancel()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getCallsWithTargetGetsCallsWithUser() = runTest {
        val now = Clock.System.now()
        val call1 = Call(1, 1, 100, Type.OUTGOING_MISSED, now)
        val call2 = Call(2, 2, 200, Type.INCOMING, now)
        val user1 = User(1, "John", "http://www.example.com/image1.jpeg")
        val user2 = User(2, "Jane", "http://www.example.com/image2.jpeg")

        callDao.saveCalls(call1, call2)
        userDao.saveUsers(user1, user2)
        callDao.getCallsWithTarget().test {
            awaitItem().also { callsWithTargets ->
                assert(callsWithTargets.size == 2)
                assert(callsWithTargets.contains(CallWithTarget(call1, user1)))
                assert(callsWithTargets.contains(CallWithTarget(call2, user2)))
                cancel()
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun cleanUp() {
        appDatabase.close()
    }
}