package com.example.calenderapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.ListItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class EventHolder(val binding: ListItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event, onEventClicked: (eventId: UUID) -> Unit) {
        binding.eventTitle.text = event.title
        binding.eventDate.text = SimpleDateFormat("EEE MMM dd hh:mm a", Locale.getDefault()).format(event.date)
        binding.eventDescription.text = event.description

        binding.root.setOnClickListener {
            onEventClicked(event.id)
        }
        //Maybe add a checkbox later
    }
}


class EventListAdapter(private var events: List<Event>, private val onEventClicked: (eventId: UUID) -> Unit) :
    RecyclerView.Adapter<EventHolder>() {

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemEventBinding.inflate(inflater, parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = events[position]
        holder.bind(event, onEventClicked)
    }


    override fun getItemCount() = events.size
}
