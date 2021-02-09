package com.nachc.dba.repository

import com.nachc.dba.firebase.FirebaseProvider
import com.nachc.dba.models.Session
import io.reactivex.rxjava3.core.Single

class SessionRepository {

    private val firebaseProvider = FirebaseProvider()

    fun saveSessionData(session: Session): Single<String> {
        return firebaseProvider.saveSessionData(session)
    }
}