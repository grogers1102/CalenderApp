package com.example.calenderapp

import android.content.Context
import androidx.room.Room
import database.EventDatabase
import database.migration_1_2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.UUID

private const val DATABASE_NAME = "event-database"

class EventRepository private constructor(context: Context, private val coroutineScope: CoroutineScope = GlobalScope) {

    private val database: EventDatabase = Room.databaseBuilder(
        context.applicationContext,
        EventDatabase::class.java,
        DATABASE_NAME
    ).addMigrations(migration_1_2).build()

    fun getEvents(): Flow<List<Event>> = database.eventDao().getEvents()

    suspend fun getEvent(id: UUID): Event = database.eventDao().getEvent(id)

    suspend fun deleteEvent(eventId: String) {
        database.eventDao().deleteEvent(eventId)
    }

    fun updateEvent(event: Event) {
        coroutineScope.launch {
            database.eventDao().updateEvent(event)
        }
    }

    suspend fun addEvent(event: Event) {
        database.eventDao().addEvent(event)
    }

    companion object {
        private var INSTANCE: EventRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = EventRepository(context)
            }
        }

        fun get(): EventRepository {
            return INSTANCE ?: throw IllegalStateException("EventRepository must be initialized")
        }
    }
}
