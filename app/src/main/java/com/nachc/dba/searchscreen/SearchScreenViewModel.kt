package com.nachc.dba.searchscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation

class SearchScreenViewModel : ViewModel() {

    /**
     * TODO:
     *  - create FirebaseService and implement getRoute method
     *  - DI with Dagger2
     * */

    private val TAG = "SearchScreenViewModel"

    val loading by lazy { MutableLiveData<Boolean>() }
    val loadError by lazy { MutableLiveData<Boolean>() }
    val validInput by lazy { MutableLiveData<Pair<Boolean, String>>() }
    val routeFetched by lazy { MutableLiveData<Boolean>() }

    // validate input
    // check for emptiness and regex match
    // set validInput value to a Pair(error: Boolean, errorMessage: String)
    private fun validateInput(input: String) {
        loading.value = false
        if (input.isEmpty() || input.isBlank()) {
            validInput.value = Pair(false, "Enter a bus line")
        } else if (!input.matches("[a-zA-Z0-9 *]+$".toRegex())) {
            validInput.value = Pair(false, "Only letters & numbers")
        } else {
            validInput.value = Pair(true, "Ok")
        }
    }

    // set goodNews value depending if route data was fetched (true) or not (false)
    // this is a placeholder until the repository is setup
    fun search(routeID: String) {
        loading.value = true
        validateInput(routeID)
        validInput.value?.first.let { valid ->
            if (valid != null && valid) {
                Log.i(TAG, "input is valid, we can search")
                getRoute(routeID)
            }
        }
    }

    // request route data from firebase service
    private fun getRoute(routeID: String) {
        Log.i(TAG, "searching getRoute")
        routeFetched.value = true
    }
}