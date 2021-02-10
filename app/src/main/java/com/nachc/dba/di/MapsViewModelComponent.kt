package com.nachc.dba.di

import com.nachc.dba.googlemaps.MapsViewModel
import dagger.Component

@Component(modules = [FirebaseModule::class])
interface MapsViewModelComponent {

    fun inject(mapsViewModel: MapsViewModel)
}