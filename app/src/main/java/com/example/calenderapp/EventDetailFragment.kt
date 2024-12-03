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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import java.io.File
import java.util.Date

class EventDetailFragment: Fragment() {

    private var _binding : FragmentEventDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "Cannot access binding because it is null. Is the view visible?"
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
        binding.apply {
            eventTitle.doOnTextChanged{ text, _, _, _ ->
                eventDetailViewModel.updateEvent { oldEvent ->
                    oldEvent.copy(title = text.toString())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                eventDetailViewModel.event.collect{ event ->
                    event?.let{updateUi(it)}
                }
            }
        }
        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE){_, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            eventDetailViewModel.updateEvent { it.copy(date = newDate) }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun updateUi(event: Event){
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
    }
}