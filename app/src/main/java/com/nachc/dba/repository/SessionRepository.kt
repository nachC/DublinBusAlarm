package com.nachc.dba.repository

import com.nachc.dba.di.DaggerSessionComponent
import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.models.Session
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SessionRepository {

    @Inject
    lateinit var firebaseProvider: FirebaseProvider

    init {
        DaggerSessionComponent.create().inject(this)
    }

    fun saveSessionData(session: Session): Single<String> {
        return firebaseProvider.saveSessionData(session)
    }
}