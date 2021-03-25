package com.nachc.dba.di

import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.repository.RouteRepository
import com.nachc.dba.repository.SessionRepository
import dagger.Module
import dagger.Provides

@Module
open class FirebaseModule {

    @Provides
    fun provideFirebaseProvider(): FirebaseProvider {
        return FirebaseProvider()
    }

    @Provides
    open fun provideRouteRepository(): RouteRepository {
        return RouteRepository()
    }

    @Provides
    fun provideSessionRepository(): SessionRepository {
        return SessionRepository()
    }
}