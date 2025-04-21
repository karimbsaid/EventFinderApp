package com.example.eventfinder.ui.adapter
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventfinder.R
import com.example.eventfinder.databinding.EventCardBinding
import com.example.eventfinder.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val eventList: List<Event>,
    private val onJoinClick: (Event) -> Unit,
    private val onFavoriteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(val binding: EventCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        val binding = holder.binding

        // Set concert title
        binding.concertTitle.text = event.name

        // Set concert address
        val venue = event._embedded.venues.firstOrNull()
        binding.concertAddress.text = venue?.address?.line1 ?: "Unknown Address"

        // Set date
        val dateParts = event.dates.start.localDate.split("-")
        if (dateParts.size == 3) {
            val month = getMonthName(dateParts[1].toInt())
            val day = dateParts[2]
            binding.dateMonth.text = month
            binding.dateDay.text = day
        }

        // Set concert price
        binding.concertPrice.text = "$40" // Mocked

        // Set image (default for now)
        val imageUrl = event.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(binding.concertImage.context)
                .load(imageUrl)
                .placeholder(R.drawable.concert) // while loading
                .into(binding.concertImage)
        } else {
            binding.concertImage.setImageResource(R.drawable.concert) // fallback
        }

        binding.joinButton.setOnClickListener {
            val venue = event._embedded.venues.firstOrNull()
            val latitude = venue?.location?.latitude?.toDouble() ?: 0.0
            val longitude = venue?.location?.longitude?.toDouble() ?: 0.0

            if (latitude != 0.0 && longitude != 0.0) {
                val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(${venue?.name})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(binding.root.context, intent, null)
            } else {
                Toast.makeText(binding.root.context, "Event location is unavailable", Toast.LENGTH_SHORT).show()
            }
        }

        binding.favoriteButton.setOnClickListener { onFavoriteClick(event) }

        binding.favoriteButton.setOnClickListener{
            val startMillis = getEventStartTimeInMillis(event.dates.start.localDate, event.dates.start.localTime)

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.Events.TITLE, event.name)
                putExtra(CalendarContract.Events.EVENT_LOCATION, venue?.name ?: "Unknown Location")
            }
            startActivity(binding.root.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
    }

    private fun getEventStartTimeInMillis(localDate: String, localTime: String?): Long {
        val dateTimeString = "$localDate ${localTime ?: "00:00:00"}"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            format.parse(dateTimeString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis() // Fallback if parsing fails
        }
    }
}
