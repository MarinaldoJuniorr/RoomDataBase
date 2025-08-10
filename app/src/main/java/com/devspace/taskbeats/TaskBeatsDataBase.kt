package com.devspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

@Database([CategoryEntity::class, TaskEntity::class], version = 3)
abstract class TaskBeatsDataBase : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getTaskDao(): TaskDao

}