package com.ignitetech.compose.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.ignitetech.compose.data.seed.DatabaseSeedWorker
import com.ignitetech.compose.utility.Constants
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class AppDatabaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var workManager: WorkManager

    @MockK
    lateinit var operation: Operation

    @MockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var sqlLiteDatabase: SupportSQLiteDatabase

    @MockK
    lateinit var databaseBuilder: RoomDatabase.Builder<AppDatabase>

    @MockK
    lateinit var databaseBuilderWithCallback: RoomDatabase.Builder<AppDatabase>

    @Test
    fun testGetInstance() {
        mockkStatic(Room::class)
        mockkStatic(WorkManager::class)

        val callbackSlot = slot<RoomDatabase.Callback>()
        val workRequestSlot = slot<WorkRequest>()

        every {
            Room.databaseBuilder(
                any(),
                any<Class<AppDatabase>>(),
                any()
            )
        } returns databaseBuilder
        every { databaseBuilder.addCallback(capture(callbackSlot)) } returns databaseBuilderWithCallback
        every { databaseBuilderWithCallback.build() } returns appDatabase
        every { WorkManager.getInstance(context) } returns workManager
        every { workManager.enqueue(capture(workRequestSlot)) } returns operation

        assertEquals(appDatabase, AppDatabase.getInstance(context))

        callbackSlot.captured.onCreate(sqlLiteDatabase)

        assertEquals(
            DatabaseSeedWorker::class.qualifiedName,
            workRequestSlot.captured.workSpec.workerClassName
        )

        verify { Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME) }
        verify { workManager.enqueue(workRequestSlot.captured) }
    }
}
