package com.example.calenderapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.CalendarDayItemBinding
import java.time.LocalDate
import java.time.ZoneId

class CalendarHolder(val binding: CalendarDayItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        day: String,
        onDayClicked: (Event?) -> Unit,
        isToday: Boolean,
        matchingEvent: Event?
    ) {
        binding.dayTextView.text = day

        if (isToday) {
            binding.dayTextView.setTextColor(Color.RED)
            binding.dayTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            binding.dayTextView.setTextColor(Color.BLACK)
            binding.dayTextView.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        binding.eventIndicator.visibility = if (matchingEvent != null) View.VISIBLE else View.GONE

        binding.root.setOnClickListener {
            if (day.isNotEmpty()) onDayClicked(matchingEvent)
        }
    }
}

class CalendarAdapter(
    private val days: List<String>,
    private val currentDay: Int?,
    private val events: List<Event>,
    private val onDayClicked: (Event?) -> Unit
) : RecyclerView.Adapter<CalendarHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarDayItemBinding.inflate(inflater, parent, false)
        return CalendarHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarHolder, position: Int) {
        val day = days[position]

        val isToday = day.toIntOrNull() == currentDay
        val matchingEvent = day.toIntOrNull()?.let { dayOfMonth ->
            events.find { event ->
                val eventDate = event.date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                eventDate.dayOfMonth == dayOfMonth &&
                        eventDate.monthValue == LocalDate.now().monthValue &&
                        eventDate.year == LocalDate.now().year
            }
        }
        holder.bind(day, onDayClicked, isToday, matchingEvent)
    }

    override fun getItemCount() = days.size
}
