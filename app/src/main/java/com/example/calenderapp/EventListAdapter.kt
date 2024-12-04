package com.example.calenderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.ListItemEventBinding
import java.util.UUID

class EventHolder(val binding: ListItemEventBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(event: Event, onEventClicked: (eventId: UUID) -> Unit){
        binding.eventTitle.text = event.title
        binding.eventDate.text = event.date.toString()

        binding.root.setOnClickListener {
            onEventClicked(event.id)
        }
        //Maybe add a checkbox later
    }
}

class EventListAdapter(private val events: List<Event>, private val onEventClicked: (eventId: UUID) -> Unit):
    RecyclerView.Adapter<EventHolder>(){
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