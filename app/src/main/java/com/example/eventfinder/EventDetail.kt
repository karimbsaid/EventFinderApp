package com.example.eventfinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.eventfinder.databinding.ActivityEventDetailBinding
import com.example.eventfinder.model.Event
import com.example.eventfinder.model.FavoriteRequest
import com.example.eventfinder.model.Venue
import com.example.eventfinder.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class EventDetail : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private lateinit var eventDetail: Event
    private var isFavorited:Boolean = false
    private var venue: Venue? = null
    private var eventId = ""
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("EVENT_ID") ?: ""

        setupButtonListeners()
        fetchEventDetails(eventId)
    }

    private fun setupButtonListeners() {
        binding.favoriteButton.setOnClickListener { toggleFavorite() }
        binding.registerButton.setOnClickListener { openTicketmasterLink() }
        binding.shareButton.setOnClickListener { shareEventLink() }
        binding.rememberMeLabel.setOnClickListener{rememberMe()}
        binding.showmapBtn.setOnClickListener{openGoogleMaps()}
        binding.backButton.setOnClickListener{    setResult(RESULT_OK)
            finish()}
    }

    private fun toggleFavorite() {
        if (!isFavorited) {
            val favoriteRequest = FavoriteRequest(eventDetail.id, eventDetail.name)
            mainViewModel.addFavoriteEvent(
                this,
                favoriteRequest,
                onSuccess = {
                    isFavorited = true
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_red)
                },
                onFailure = {
                    Toast.makeText(this, "Failed to add favorite", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            mainViewModel.removeFavoriteEvent(
                this,
                eventId,
                onSuccess = {
                    isFavorited = false
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
                },
                onFailure = {
                    Toast.makeText(this, "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    private fun openTicketmasterLink() {
        val url = eventDetail.url
        if (url.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun openGoogleMaps() {
        val latitude = venue?.location?.latitude?.toDoubleOrNull()
        val longitude = venue?.location?.longitude?.toDoubleOrNull()

        if (latitude != null && longitude != null) {
            val uri = "geo:$latitude,$longitude?q=$latitude,$longitude(${venue?.name})"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Event location is unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareEventLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, eventDetail.url)
        }
        startActivity(Intent.createChooser(shareIntent, "Share event link via"))
    }

    private fun fetchEventDetails(eventId: String) {
        mainViewModel.fetchEventDetail(eventId) { event ->
            if (event != null) {
                eventDetail = event
                displayEventDetails(event)
            } else {
                showErrorState()
            }
        }
        mainViewModel.isFavorite(this, eventId) { favoriteItem ->
            Log.d("isfavoritedddd",eventId)
            if (favoriteItem != null) {
                isFavorited = true

                // Optionally, update UI to reflect it's favorite
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_red)
            } else {
                isFavorited = false
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
            }
        }

    }

    private fun displayEventDetails(event: Event) {
        binding.eventTitle.text = event.name
        binding.organizer.text = "By ${event.promoter?.name ?: "Unknown"}"
        binding.categoryPill.text = event.classifications?.firstOrNull()?.segment?.name ?: "Unknown"
        binding.eventDate.text = event.dates.start.localDate
        binding.eventTime.text = event.dates.start.localTime
        binding.eventInfo.text = event.accessibility?.info ?: "-----"

        venue = event._embedded.venues.firstOrNull()
        binding.eventLocation.text = venue?.let {
            "${it.name}, ${it.address?.line1 ?: "Unknown"}, ${it.city?.name ?: ""}, ${it.state?.name ?: ""}"
        } ?: "Address: Unknown"

        Glide.with(this)
            .load(event.images.firstOrNull()?.url)
            .placeholder(R.drawable.concert)
            .into(binding.eventImage)
    }

    private fun showErrorState() {
        Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show()
        binding.favoriteButton.visibility = View.GONE
        binding.categoryPill.visibility = View.GONE
        binding.eventDate.visibility = View.GONE
        binding.eventLocation.visibility = View.GONE
    }

    private fun getEventStartTimeInMillis(localDate: String, localTime: String?): Long {
        val dateTimeString = "$localDate ${localTime ?: "00:00:00"}"
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            format.parse(dateTimeString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun rememberMe() {
        val startMillis = getEventStartTimeInMillis(
            eventDetail.dates.start.localDate,
            eventDetail.dates.start.localTime
        )

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.Events.TITLE, eventDetail.name)
            putExtra(CalendarContract.Events.EVENT_LOCATION, venue?.name ?: "Unknown Location")
        }
        startActivity(intent)
    }


}
