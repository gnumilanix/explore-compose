package com.ignitetech.compose.di

import com.ignitetech.compose.data.AppDatabase
import com.ignitetech.compose.data.call.CallDao
import com.ignitetech.compose.data.chat.ChatDao
import com.ignitetech.compose.data.user.UserDao
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DaoModuleTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var appDatabase: AppDatabase

    @MockK
    lateinit var userDao: UserDao

    @MockK
    lateinit var callDao: CallDao

    @MockK
    lateinit var chatDao: ChatDao

    private val daoModule = DaoModule()

    @Test
    fun provideUserDao() {
        every { appDatabase.userDao() } returns userDao

        assertEquals(userDao, daoModule.provideUserDao(appDatabase))
    }

    @Test
    fun provideChatDao() {
        every { appDatabase.chatDao() } returns chatDao

        assertEquals(chatDao, daoModule.provideChatDao(appDatabase))
    }

    @Test
    fun provideCallDao() {
        every { appDatabase.callDao() } returns callDao

        assertEquals(callDao, daoModule.provideCallDao(appDatabase))
    }
}
