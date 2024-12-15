package com.example.calenderapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventListViewModel: ViewModel() {
    private val eventRepository = EventRepository.get()
    private val _event: MutableStateFlow<List<Event>> = MutableStateFlow(emptyList())
    val events: StateFlow<List<Event>>
        get() = _event.asStateFlow()

    init{
        viewModelScope.launch {
            eventRepository.getEvents().collect { events ->
                Log.d("EventListViewModel", "Fetched events: $events")
                _event.value = events
            }
        }
    }
    suspend fun addEvent(event: Event){
        eventRepository.addEvent(event)
        _event.value = _event.value + event
    }
}