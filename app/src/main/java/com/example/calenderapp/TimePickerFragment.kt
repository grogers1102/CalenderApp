package com.example.calenderapp

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var listener: OnTimeSelectedListener? = null

    interface OnTimeSelectedListener {
        fun onTimeSelected(hour: Int, minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            true // false for 24-hour format, true for 12-hour
        )
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener?.onTimeSelected(hourOfDay, minute)
    }
}
