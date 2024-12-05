package com.example.calenderapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EventDetailViewModel(eventId: UUID) : ViewModel() {
    private val eventRepository = EventRepository.get()

    private val _event: MutableStateFlow<Event?> = MutableStateFlow(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    init {
        viewModelScope.launch {
            _event.value = eventRepository.getEvent(eventId)
        }
    }

    fun updateEvent(onUpdate: (Event) -> Event) {
        _event.update { oldEvent -> oldEvent?.let { onUpdate(it) } }
    }

    // Save the event (e.g., when user presses the submit button)
    suspend fun saveEvent(event: Event) {
        eventRepository.addEvent(event)
    }

    override fun onCleared() {
        super.onCleared()
        // Update the event in the repository when the ViewModel is cleared
        event.value?.let { eventRepository.updateEvent(it) }
    }
}

class EventDetailViewModelFactory(private val eventId: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventDetailViewModel(eventId) as T
    }
}
