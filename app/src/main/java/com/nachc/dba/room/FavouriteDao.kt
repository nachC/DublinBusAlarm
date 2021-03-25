package com.nachc.dba.room

import androidx.room.*
import com.nachc.dba.models.Favourite

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM Favourites")
    suspend fun getAllFav(): List<Favourite>?

    @Query("SELECT * FROM Favourites WHERE id=:id")
    suspend fun getFav(id: String): Favourite?

    @Query("SELECT EXISTS (SELECT * FROM Favourites WHERE id=:id)")
    suspend fun favExists(id: String): Boolean?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFav(favourite: Favourite): Long?

    @Delete
    suspend fun deleteFav(favourite: Favourite)

    @Query("DELETE FROM Favourites WHERE id=:id")
    suspend fun deleteFavById(id: String)
}