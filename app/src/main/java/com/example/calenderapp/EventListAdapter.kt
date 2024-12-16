import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calenderapp.Event
import com.example.calenderapp.databinding.ListItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class EventListAdapter(
    private val formattedEvents: List<Pair<Event, Pair<String, String>>>,
    private val onEventClicked: (eventId: UUID) -> Unit
) : RecyclerView.Adapter<EventListAdapter.EventHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemEventBinding.inflate(inflater, parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val (event, formattedDateTime) = formattedEvents[position]
        val (formattedDate, formattedTime) = formattedDateTime
        holder.bind(event, formattedDate, formattedTime, onEventClicked)
    }

    override fun getItemCount() = formattedEvents.size

    inner class EventHolder(private val binding: ListItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, formattedDate: String, formattedTime: String, onEventClicked: (UUID) -> Unit) {
            binding.eventTitle.text = event.title
            binding.eventDescription.text = event.description
            binding.eventDate.text = formattedDate
            binding.eventTime.text = formattedTime

            binding.root.setOnClickListener {
                onEventClicked(event.id)
            }
        }
    }
}