package com.example.calenderapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import java.util.Date

@Entity(tableName = "event")
data class Event(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val date: Date
)
