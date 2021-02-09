package com.nachc.dba.googlemaps

import android.util.Log
import androidx.lifecycle.ViewModel
import com.nachc.dba.models.Session
import com.nachc.dba.repository.SessionRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsViewModel : ViewModel() {

    private val TAG = "MapsFragmentViewModel"

    private val sessionRepository = SessionRepository()
    private val disposable = CompositeDisposable()

    fun saveSessionData(session: Session) {
        disposable.add(
            sessionRepository.saveSessionData(session)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<String>() {
                    override fun onSuccess(t: String?) {
                        Log.i(TAG, t!!)
                    }

                    override fun onError(e: Throwable?) {
                        Log.i(TAG, e!!.message!!)
                    }
                }))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}