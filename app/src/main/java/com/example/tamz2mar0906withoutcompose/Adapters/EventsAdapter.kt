import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tamz2mar0906withoutcompose.R
import com.example.tamz2mar0906withoutcompose.Http.EventResponseModel
import java.util.Locale

class EventsAdapter(
    private val context: Context,
    private var events: List<EventResponseModel>,
    private val onEventClick: (EventResponseModel) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.eventNameTextView)
        val eventDate: TextView = view.findViewById(R.id.eventDateTextView)
        val eventLocation: TextView = view.findViewById(R.id.eventLocationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.eventName
        holder.eventDate.text = event.eventDate
        holder.eventLocation.text = event.location

        val currentTime = System.currentTimeMillis()
        val eventTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(event.eventDate)?.time

        if (eventTime != null && eventTime < currentTime) {
            holder.itemView.alpha = 0.6f
            holder.itemView.setBackgroundResource(R.drawable.event_item_background)
        } else {
            holder.itemView.alpha = 1.0f
            holder.itemView.setBackgroundResource(R.drawable.event_item_background)
        }

        holder.itemView.setOnClickListener {
            onEventClick(event)
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<EventResponseModel>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
