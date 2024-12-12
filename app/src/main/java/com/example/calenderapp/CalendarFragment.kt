package com.example.calenderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.databinding.CalendarGridBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment: Fragment() {
    private var _binding: CalendarGridBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val calendarViewModel: CalendarViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarGridBinding.inflate(inflater, container, false)
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the current month and year
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthName = dateFormat.format(calendar.time)

        // Set the month name in the TextView
        binding.monthTextView.text = monthName
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        // Initialize RecyclerView here as usual
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.adapter = CalendarAdapter(
            generateDaysForMonth(calendar),
            currentDay = today,
            onDayClicked = { day ->
                Toast.makeText(context, "Clicked: $day", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun generateDaysForMonth(calendar: Calendar): List<String> {
        // Same logic as before to generate days with placeholders
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val days = MutableList(firstDayOfWeek) { "" } // Empty placeholders
        days.addAll((1..daysInMonth).map { it.toString() })
        return days
    }
}