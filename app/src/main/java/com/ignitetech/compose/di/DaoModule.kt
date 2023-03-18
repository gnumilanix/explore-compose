package com.ignitetech.compose.di

import com.ignitetech.compose.data.AppDatabase
import com.ignitetech.compose.data.call.CallDao
import com.ignitetech.compose.data.chat.ChatDao
import com.ignitetech.compose.data.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Singleton
    @Provides
    fun provideChatDao(appDatabase: AppDatabase): ChatDao {
        return appDatabase.chatDao()
    }

    @Singleton
    @Provides
    fun provideCallDao(appDatabase: AppDatabase): CallDao {
        return appDatabase.callDao()
    }
}
