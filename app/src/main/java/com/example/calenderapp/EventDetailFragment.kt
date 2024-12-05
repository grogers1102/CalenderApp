package com.example.calenderapp

import androidx.fragment.app.Fragment
import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.calenderapp.databinding.FragmentEventDetailBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.room.Room
import database.EventDatabase
import database.migration_1_2
import java.io.File
import java.util.Date
import java.util.UUID

class EventDetailFragment: Fragment() {

    private var _binding : FragmentEventDetailBinding? = null
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

        binding.eventSubmit.setOnClickListener {
            val title = binding.eventTitle.text.toString()
            val description = binding.eventDescription.text.toString()
            val date = System.currentTimeMillis()

            if (title.isNotBlank()) {
                val newEvent = Event(UUID.randomUUID(), title, description, Date(date))
                saveEvent(newEvent)
            } else {
                Toast.makeText(requireContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
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
    /*private fun updateUi(event: Event){
        binding.apply {
            if(eventTitle.text.toString() != event.title){
                eventTitle.setText(event.title)
            }
            eventDate.text = event.date.toString()
            eventDate.setOnClickListener {
                findNavController().navigate(
                    EventDetailFragmentDirections.selectDate(event.date)
                )
            }
            if(eventDescription.text.toString() != event.description){
                eventDescription.setText(event.description)
            }
        }
    }*/
}