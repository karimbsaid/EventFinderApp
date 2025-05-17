package com.example.eventfinder.ui.adapter
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
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
    private val onEventClick: (Event) -> Unit,
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
Log.d("isfavorited",event.isFavorited.toString())
        binding.title.text = event.name
        binding.tag.text = event.classifications?.get(0)?.segment?.name

        val venue = event._embedded.venues.firstOrNull()
        binding.eventAddress.text =
            (venue?.state?.name + " , " + venue?.city?.name + " , " + venue?.address?.line1)
                ?: "Unknown Address"

        val dateParts = event.dates.start.localDate
       binding.eventDate.text=dateParts + " "+ event.dates.start.localTime

        val favoriteIcon = if (event.isFavorited) R.drawable.ic_favorite_red else R.drawable.ic_favorite_white
        binding.favoriteButton.setImageResource(favoriteIcon)


        val imageUrl = event.images.firstOrNull()?.url
        if (imageUrl != null) {
            Glide.with(binding.eventImage.context)
                .load(imageUrl)
                .placeholder(R.drawable.concert)
                .into(binding.eventImage)
        } else {
            binding.eventImage.setImageResource(R.drawable.concert)
        }
        binding.root.setOnClickListener{
            onEventClick(event)
        }


        binding.favoriteButton.setOnClickListener {
            onFavoriteClick(event)
        }

    }

    override fun getItemCount(): Int {
        return eventList.size
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
