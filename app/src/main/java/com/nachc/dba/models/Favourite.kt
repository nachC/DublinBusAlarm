package com.nachc.dba.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class Favourite(
    @PrimaryKey val id: String,
    val direction: String,
    val trip: String
)
