package com.ignitetech.compose.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.TimeZone
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SystemModule {
    @Singleton
    @Provides
    fun provideTimezone(): TimeZone {
        return TimeZone.currentSystemDefault()
    }
}
