package com.example.echoposts.di

import com.example.echoposts.data.local.UserSession
import com.example.echoposts.data.local.dao.UserDao
import com.example.echoposts.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        userDao: UserDao,
        userSession: UserSession
    ): AuthRepository {
        return AuthRepository(userDao, userSession)
    }


}