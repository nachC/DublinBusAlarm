package com.nachc.dba.searchscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchScreenViewModel : ViewModel() {

    /**
     * TODO:
     * */

    private val TAG = "SearchScreenViewModel"

    val routeID: MutableLiveData<String> = MutableLiveData()

    // _goodNews is true if we were able to retrieve route data from the DB
    // with LiveData we share our _goodNews with the observing Observers
    private val _goodNews = MutableLiveData<Boolean>()
    val news: LiveData<Boolean>
        get() = _goodNews

    private val _isValidInput = MutableLiveData<Pair<Boolean, String>>()
    val isValidInput: LiveData<Pair<Boolean, String>>
        get() = _isValidInput

    // validate input
    // check for emptiness and regex match
    // return a Pair(error: Boolean, errorMessage: String)
    fun validateInput(input: String) {
        if (input.isEmpty() || input.isBlank()) {
            _isValidInput.value = Pair(false, "Enter a bus line")
        } else if (!input.matches("[a-zA-Z0-9 *]+$".toRegex())) {
            _isValidInput.value = Pair(false, "Only letters & numbers")
        } else {
            _isValidInput.value = Pair(true, "Ok")
        }
    }

    // set goodNews value depending if route data was fetched (true) or not (false)
    // this is a placeholder until the repository is setup
    fun search(routeID: String) {
        Log.i(TAG, routeID)
        _goodNews.value = true
    }
}