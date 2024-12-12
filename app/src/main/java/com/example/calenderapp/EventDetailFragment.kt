package com.example.calenderapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

class EventDetailFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

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

    private var selectedDate: Long = System.currentTimeMillis()

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

        binding.eventDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.eventSubmit.setOnClickListener {
            val title = binding.eventTitle.text.toString()
            val description = binding.eventDescription.text.toString()

            if (title.isNotBlank()) {
                val newEvent = Event(UUID.randomUUID(), title, description, Date(selectedDate))
                saveEvent(newEvent)

                // Show calendar when event is submitted
                (activity as? MainActivity)?.showCalendar()
            } else {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            // Navigate back to the previous fragment (MainActivity)
            parentFragmentManager.popBackStack()

            // If the parent activity is MainActivity, explicitly show the calendar
            (activity as? MainActivity)?.showCalendar()
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
}
