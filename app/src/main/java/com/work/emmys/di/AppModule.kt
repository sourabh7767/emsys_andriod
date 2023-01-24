package com.work.emmys.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Created by PaL on 20,January,2023
 */

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideContext(): ApplicationContext {
        return ApplicationContext()
    }
}