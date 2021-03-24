package com.nachc.dba

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import androidx.lifecycle.*
import com.nachc.dba.models.Favourite
import com.nachc.dba.models.Trip
import com.nachc.dba.repository.FavouriteRepository
import com.nachc.dba.repository.RouteRepository
import com.nachc.dba.room.AppDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val TAG = "MainViewModel"

    private val routeRepository = RouteRepository()
    private val favouriteRepository = FavouriteRepository(AppDatabase.getInstance(getApplication()))

    val loading by lazy { MutableLiveData<Boolean>() }
    val loadError by lazy { MutableLiveData<Pair<Boolean, String>>() }
    val validInput by lazy { MutableLiveData<Pair<Boolean, String>>() }
    val trips by lazy { MutableLiveData<List<Trip>>() }
    val routeName by lazy { MutableLiveData<String>() }
    val favourites by lazy { MutableLiveData<List<Favourite>>() }
    val latestID by lazy { MutableLiveData<Long>() }

    private val disposable = CompositeDisposable()

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

    fun resetTrips() {
        trips.value = null
        routeName.value = ""
    }

    fun getAllFavs() {
        viewModelScope.launch {
            favourites.value = favouriteRepository.getAllFavs()
        }
    }

    fun getFavById(id: String) {
        viewModelScope.launch {
            favouriteRepository.getFavById(id)
        }
    }

    fun saveFavourite(id: String, direction: String, trip: String) {
        viewModelScope.launch {
            val fav = Favourite(id, direction, trip)
            latestID.value = favouriteRepository.saveFav(fav)
            getAllFavs()
        }
    }

    fun deleteFavourite(favourite: Favourite) {
        viewModelScope.launch {
            favouriteRepository.deleteFav(favourite)
            getAllFavs()
        }
    }

    fun deleteFavouriteById(id: String) {
        viewModelScope.launch {
            favouriteRepository.deleteFavById(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}