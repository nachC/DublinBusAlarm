package com.nachc.dba.searchscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nachc.dba.di.DaggerSearchViewModelComponent
import com.nachc.dba.models.Trip
import com.nachc.dba.repository.RouteRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SearchScreenViewModel(test: Boolean = true) : ViewModel() {

    private val TAG = "SearchScreenViewModel"

    val loading by lazy { MutableLiveData<Boolean>() }
    val loadError by lazy { MutableLiveData<Pair<Boolean, String>>() }
    val validInput by lazy { MutableLiveData<Pair<Boolean, String>>() }
    val trips by lazy { MutableLiveData<List<Trip>>() }

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var routeRepository: RouteRepository

    private var injected = false

    init {
        if (!injected) {
            DaggerSearchViewModelComponent.create().inject(this)
        }
    }

    // check for emptiness and regex match
    // set validInput value to a Pair(error: Boolean, errorMessage: String)
    fun validateInput(input: String) {
        if (input.isEmpty() || input.isBlank()) {
            validInput.value = Pair(false, "Enter a bus line")
            loading.value = false
        } else if (!input.matches("[a-zA-Z0-9 *]+$".toRegex())) {
            validInput.value = Pair(false, "Only letters & numbers")
            loading.value = false
        } else {
            validInput.value = Pair(true, "")
        }
    }

    fun search(routeID: String) {
        loading.value = true
        validateInput(routeID)
        if (validInput.value!!.first) {
            disposable.add(
                routeRepository.getRouteData(routeID)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<List<Trip>>() {
                        override fun onSuccess(_trips: List<Trip>?) {
                            trips.value = _trips
                            loading.value = false
                            loadError.value = Pair(false, "")
                        }

                        override fun onError(e: Throwable?) {
                            loading.value = false
                            loadError.value = Pair(true, e?.message!!)
                        }

                    })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun resetTrips() {
        trips.value = null
    }

    init {
        injected = true
    }
}