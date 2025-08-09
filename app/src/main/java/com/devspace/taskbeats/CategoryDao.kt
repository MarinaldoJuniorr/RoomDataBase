package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {

    @Query("Select * From categoryEntity")
    fun getAll(): List<CategoryEntity>

    @Insert
    fun insetAll(categoryEntity: List<CategoryEntity>)
}