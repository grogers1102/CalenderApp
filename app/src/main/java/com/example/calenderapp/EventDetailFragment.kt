package com.example.calenderapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.calenderapp.databinding.FragmentEventDetailBinding
import database.EventDatabase
import database.migration_1_2
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class EventDetailFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerFragment.OnTimeSelectedListener {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    private val database by lazy {
        Room.databaseBuilder(
            requireContext(),
            EventDatabase::class.java,
            "event_database"
        ).addMigrations(migration_1_2)
            .build()
    }

    private val args: EventDetailFragmentArgs by navArgs()

    private val eventDetailViewModel: EventDetailViewModel by viewModels {
        EventDetailViewModelFactory(args.eventId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the hint of the button to the current date
        val currentDate = getCurrentFormattedDate()
        binding.eventDate.hint = currentDate

        val currentCalendar = Calendar.getInstance()
        selectedHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        selectedMinute = currentCalendar.get(Calendar.MINUTE)
        updateEventTimeDisplay()

        binding.eventDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.eventTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.eventSubmit.setOnClickListener {
            val title = binding.eventTitle.text.toString()
            val description = binding.eventDescription.text.toString()

            if (title.isNotBlank()) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.HOUR_OF_DAY, selectedMinute)
                val newEvent = Event(UUID.randomUUID(), title, description, calendar.time)
                saveEvent(newEvent)
            } else {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateEventTimeDisplay() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        binding.eventTime.text = timeFormat.format(calendar.time)
    }

    private fun showTimePickerDialog() {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.listener = this
        timePickerFragment.show(parentFragmentManager, "timePicker")
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        binding.eventTime.text = timeFormat.format(calendar.time)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.timeInMillis

        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        binding.eventDate.text = sdf.format(Date(selectedDate))
    }

    private fun saveEvent(event: Event) {
        lifecycleScope.launch {
            try {
                database.eventDao().addEvent(event)
                Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error saving event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun updateUi(event: Event){
        binding.apply {
            if(eventTitle.text.toString() != event.title){
                eventTitle.setText(event.title)
            }
            eventDate.text = event.date.toString()
            eventDate.setOnClickListener{
                findNavController().navigate(
                    EventDetailFragmentDirections.selectDate(event.date)
                )
            }
            if(eventDescription.text.toString() != event.description){
                eventDescription.setText(event.description)
            }
        }
    }
}
