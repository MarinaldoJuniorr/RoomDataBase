package com.devspace.taskbeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["Key"]   ,
            childColumns = ["category"]   ,
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(true)
    val id: Long = 0,
    val category: String,
    val name: String,
)
