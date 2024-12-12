package com.example.calenderapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.CalendarDayItemBinding
import java.time.DayOfWeek
class CalendarHolder(val binding: CalendarDayItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(day: String, onDayClicked: (String) -> Unit, isToday: Boolean) {
        binding.dayTextView.text = day
        if (isToday) {
            binding.dayTextView.setTextColor(Color.RED) // Highlight today in red
        } else {
            binding.dayTextView.setTextColor(Color.BLACK) // Default color
        }
        binding.dayTextView.isClickable = day.isNotEmpty()
        binding.root.setOnClickListener {
            if (day.isNotEmpty()) {
                onDayClicked(day)
            }
        }
    }
}

class CalendarAdapter(
    private val days: List<String>,
    private val currentDay: Int?,
    private val onDayClicked: (String) -> Unit
) : RecyclerView.Adapter<CalendarHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalendarDayItemBinding.inflate(inflater, parent, false)
        return CalendarHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarHolder, position: Int) {
        val day = days[position]
        val isToday = day.isNotEmpty() && day.toIntOrNull() == currentDay
        holder.bind(day, onDayClicked, isToday)

        // Log for debugging
        Log.d("CalendarAdapter", "Binding day: $day at position: $position")
    }

    override fun getItemCount(): Int {
        Log.d("CalendarAdapter", "Total days: ${days.size}")
        return days.size
    }
}