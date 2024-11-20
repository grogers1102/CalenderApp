package com.example.calenderapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
class CalendarAdapter(private val calendarData: List<Pair<DayOfWeek, Int?>>):
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>(){
    class CalendarViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val dayTextView: TextView = view.findViewById((R.id.dayTextView))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_day_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val dayData = calendarData[position]
        holder.dayTextView.text = dayData.second?.toString() ?: ""
    }

    override fun getItemCount() = calendarData.size
}