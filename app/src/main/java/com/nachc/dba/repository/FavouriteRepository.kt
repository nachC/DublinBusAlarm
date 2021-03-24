package com.nachc.dba.repository

import com.nachc.dba.models.Favourite
import com.nachc.dba.room.AppDatabase

class FavouriteRepository(private val database: AppDatabase) {

    suspend fun getAllFavs() = database.favouriteDao.getAllFav()

    suspend fun getFavById(id: String) = database.favouriteDao.getFav(id)

    suspend fun favExists(id: String) = database.favouriteDao.favExists(id)

    suspend fun saveFav(favourite: Favourite) = database.favouriteDao.saveFav(favourite)

    suspend fun deleteFav(favourite: Favourite) = database.favouriteDao.deleteFav(favourite)

    suspend fun deleteFavById(id: String) = database.favouriteDao.deleteFavById(id)
}