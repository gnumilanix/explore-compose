package com.ignitetech.compose.di

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.ignitetech.compose.data.AppDatabase
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
internal class DataModuleTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var file: File

    @MockK
    lateinit var dataStore: DataStore<Preferences>

    private val dataModule = DataModule()

    @Test
    fun provideDatabase() {
        mockkObject(AppDatabase)

        every { AppDatabase.getInstance(context) } returns appDatabase

        assertEquals(appDatabase, dataModule.provideDatabase(context))
    }

    @Test
    fun provideDataStore() = runTest {
        val emptyPreferences = mockk<Preferences>()
        val produceFileSlot = slot<() -> File>()
        val corruptionHandlerSlot = slot<ReplaceFileCorruptionHandler<Preferences>>()
        val migrationsSlot = slot<List<DataMigration<Preferences>>>()
        val scopeSlot = slot<CoroutineScope>()

        mockkStatic("androidx.datastore.preferences.PreferenceDataStoreFile")
        mockkStatic("androidx.datastore.preferences.core.PreferencesFactory")
        mockkObject(PreferenceDataStoreFactory)
        every {
            PreferenceDataStoreFactory.create(
                capture(corruptionHandlerSlot),
                capture(migrationsSlot),
                capture(scopeSlot),
                capture(produceFileSlot)
            )
        } returns dataStore
        every { emptyPreferences() } returns emptyPreferences
        every { context.preferencesDataStoreFile(any()) } returns file

        assertEquals(dataStore, dataModule.provideDataStore(context))
        assertEquals(0, migrationsSlot.captured.size)
        assertEquals(emptyPreferences, corruptionHandlerSlot.captured.handleCorruption(mockk()))
        assertEquals(Dispatchers.IO, scopeSlot.captured.coroutineContext[CoroutineDispatcher.Key])
        assertEquals(file, produceFileSlot.invoke())
    }
}