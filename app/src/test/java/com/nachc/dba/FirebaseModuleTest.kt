package com.nachc.dba

import com.nachc.dba.di.FirebaseModule
import com.nachc.dba.repository.RouteRepository

class FirebaseModuleTest(val mockRouteRepo: RouteRepository): FirebaseModule() {
    override fun provideRouteRepository(): RouteRepository {
        return mockRouteRepo
    }
}