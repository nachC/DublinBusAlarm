package com.nachc.dba.di

import com.nachc.dba.repository.SessionRepository
import dagger.Component

@Component(modules = [FirebaseModule::class])
interface SessionComponent {

    fun inject(sessionRepo: SessionRepository)
}