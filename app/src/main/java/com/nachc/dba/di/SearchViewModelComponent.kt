package com.nachc.dba.di

import com.nachc.dba.searchscreen.SearchScreenViewModel
import dagger.Component

@Component(modules = [FirebaseModule::class])
interface SearchViewModelComponent {

    fun inject(searchViewModel: SearchScreenViewModel)
}