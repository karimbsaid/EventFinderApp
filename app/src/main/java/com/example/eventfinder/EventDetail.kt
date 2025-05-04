package com.example.eventfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.eventfinder.databinding.ActivityEventDetailBinding
import com.example.eventfinder.databinding.ActivityMainBinding
import com.example.eventfinder.databinding.FragmentHomeBinding
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.Venue
import com.example.eventfinder.viewmodel.EventDetailVM
import com.example.eventfinder.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetail : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private lateinit var event: Event
    private  var eventId = ""
    private var eventName = ""
    private var eventThumb = ""
    private  var venue : Venue?=null
    private val viewModel: EventDetailVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getEventInfoFromIntent()
        loadEventImage(eventThumb)
        fetchEventDetails(eventId)
        observeEventDetails()
        setupButtonListeners()
    }

    private fun observeEventDetails() {
        viewModel.eventDetail.observe(this) { event ->
            if (event != null) {
                this.event = event
                displayEventDetails(event)
            } else {
                showErrorState()
            }
        }
    }
    private fun loadEventImage(imageUrl: String?) {
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.concert)
                .into(binding.eventImage)
            binding.eventImage.visibility = View.VISIBLE
        } else {
            binding.eventImage.setImageResource(R.drawable.concert)
            binding.eventImage.visibility = View.VISIBLE
        }
    }

    private fun getEventInfoFromIntent() {
        val tempIntent = intent

        this.eventId = tempIntent.getStringExtra("EVENT_ID")!!
        this.eventName = tempIntent.getStringExtra("EVENT_NAME")!!
        this.eventThumb = tempIntent.getStringExtra("EVENT_THUMB")!!
    }

    private fun setupButtonListeners() {
        binding.favoriteButton.setOnClickListener { toggleFavourite()}
        binding.bookButton.setOnClickListener { openTicketmasterLink() }
        binding.mapButton.setOnClickListener { openGoogleMaps() }
        binding.rememberMeButton.setOnClickListener{remeberMe()}
    }

    private fun toggleFavourite() {
        viewModel.addFavoriteEvent(this,event)
        Toast.makeText(binding.root.context, "Event added", Toast.LENGTH_SHORT).show()


    }

    private fun openGoogleMaps() {

            val latitude = venue?.location?.latitude?.toDouble() ?: 0.0
            val longitude = venue?.location?.longitude?.toDouble() ?: 0.0

            if (latitude != 0.0 && longitude != 0.0) {
                val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(${venue?.name})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                ContextCompat.startActivity(binding.root.context, intent, null)
            } else {
                Toast.makeText(binding.root.context, "Event location is unavailable", Toast.LENGTH_SHORT).show()
            }


    }

    private fun openTicketmasterLink() {
        val ticketmasterUrl = event.url
        if (ticketmasterUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ticketmasterUrl))
            startActivity(intent)
        }
    }



    private fun showErrorState() {
        binding.favoriteButton.visibility = View.GONE
        binding.eventCategory.visibility = View.GONE
        binding.eventDate.visibility = View.GONE
        binding.eventAddress.visibility = View.GONE
        binding.bookButton.visibility = View.GONE
        binding.mapButton.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = "Failed to load event details"
    }

    private fun displayEventDetails(event: Event) {
        binding.favoriteButton.visibility = View.VISIBLE
        binding.eventCategory.visibility = View.VISIBLE
        binding.eventDate.visibility = View.VISIBLE
        binding.eventAddress.visibility = View.VISIBLE
        binding.bookButton.visibility = View.VISIBLE
        binding.mapButton.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE

        binding.eventCategory.text = "Category: ${event.classifications?.firstOrNull()?.segment?.name ?: "Unknown"}"
        binding.eventDate.text = "Date: ${event.dates.start.localDate} at ${event.dates.start.localTime ?: "TBA"}"
        venue = event._embedded.venues.firstOrNull()
        binding.eventAddress.text = venue?.let {
            "Address: ${it.name}, ${it.address?.line1 ?: "Unknown"}, ${it.city?.name ?: ""}, ${it.state?.name ?: ""}"
        } ?: "Address: Unknown"
    }

    private fun fetchEventDetails(eventId: String) {
        viewModel.fetchDetailById(eventId)
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


    private fun remeberMe(){
            val startMillis = getEventStartTimeInMillis(event.dates.start.localDate, event.dates.start.localTime)

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.Events.TITLE, event.name)
                putExtra(CalendarContract.Events.EVENT_LOCATION, venue?.name ?: "Unknown Location")
            }
            ContextCompat.startActivity(binding.root.context, intent, null)
        }



}