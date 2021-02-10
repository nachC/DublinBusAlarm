package com.nachc.dba.di

import com.nachc.dba.repository.RouteRepository
import dagger.Component

@Component(modules = [FirebaseModule::class])
interface RouteComponent {

    fun inject(routeRepo: RouteRepository)
}