package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {

    @Query("Select * From categoryEntity")
    fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetAll(categoryEntity: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inset(categoryEntity: CategoryEntity)
}