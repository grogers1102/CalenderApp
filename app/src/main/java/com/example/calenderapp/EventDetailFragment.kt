package com.example.calenderapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.calenderapp.databinding.FragmentEventDetailBinding
import kotlinx.coroutines.launch
import java.util.Date

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: EventDetailFragmentArgs by navArgs()

    private val eventDetailViewModel: EventDetailViewModel by viewModels {
        EventDetailViewModelFactory(args.eventId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes to event data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventDetailViewModel.event.collect { event ->
                    event?.let { updateUi(it) }
                }
            }
        }

        // Update event title as the user types
        binding.eventTitle.doOnTextChanged { text, _, _, _ ->
            eventDetailViewModel.updateEvent { oldEvent ->
                oldEvent.copy(title = text.toString())
            }
        }

        // Set up listener for date selection
        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            eventDetailViewModel.updateEvent { it.copy(date = newDate) }
        }

        // Handle Submit button click
        binding.eventSubmit.setOnClickListener {
            saveEvent()
        }

        // Handle Back to Main button click
        binding.backToMain.setOnClickListener {
            // Navigate back to the event list (or root activity)
            findNavController().popBackStack(R.id.eventListFragment, false)
        }
    }

    private fun saveEvent() {
        // Save the event locally when "Submit" is clicked
        eventDetailViewModel.event.value?.let { event ->
            lifecycleScope.launch {
                eventDetailViewModel.saveEvent(event)
                // After saving, navigate back to the event list
                findNavController().navigateUp()
            }
        }
    }

    private fun updateUi(event: Event) {
        binding.apply {
            if (eventTitle.text.toString() != event.title) {
                eventTitle.setText(event.title)
            }
            eventDate.text = event.date.toString()
            eventDate.setOnClickListener {
                findNavController().navigate(
                    EventDetailFragmentDirections.selectDate(event.date)
                )
            }
            if (eventDescription.text.toString() != event.description) {
                eventDescription.setText(event.description)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
