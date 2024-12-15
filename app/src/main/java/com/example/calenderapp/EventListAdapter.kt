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

/*class EventHolder(val binding: ListItemEventBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(event: Event, onEventClicked: (eventId: UUID) -> Unit){
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        Log.d("EventListAdapter", "Binding event: ${event.title}")

        binding.eventTitle.text = event.title
        binding.eventDescription.text = event.description
        binding.eventDate.text = dateFormat.format(event.date)
        binding.eventTime.text = timeFormat.format(event.date)

        binding.root.setOnClickListener {
            onEventClicked(event.id)
        }
    }
}*/

class EventListAdapter(private val events: List<Event>, private val onEventClicked: (eventId: UUID) -> Unit):
    RecyclerView.Adapter<EventListAdapter.EventHolder>(){
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

    inner class inner class EventHolder(private val binding: ListItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, onEventClicked: (UUID) -> Unit) {
            binding.eventTitle.text = event.title
            binding.eventDescription.text = event.description
            binding.eventDate.text = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(event.date)
            binding.eventTime.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(event.date)

            binding.root.setOnClickListener {
                onEventClicked(event.id)
            }
        }
    }
}