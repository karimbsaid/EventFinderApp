package com.example.eventfinder.model

// models/Event.kt
data class EventResponse(
    val _embedded: EmbeddedEvents?
)

data class EmbeddedEvents(
    val events: List<Event>
)

data class Event(
    val id: String,
    val name: String,
    val dates: EventDates,
    val _embedded: EventEmbedded,
    val images: List<EventImage> // Add images field

)
data class EventImage(
    val ratio: String,
    val url: String
)
data class EventDates(
    val start: EventStart
)

data class EventStart(
    val localDate: String,
    val localTime: String?
)

data class EventEmbedded(
    val venues: List<Venue>
)

data class Venue(
    val name: String,
    val address: Address?,
    val location: Location?
)

data class Address(
    val line1: String?
)

data class Location(
    val latitude: String?,
    val longitude: String?
)

data class Classification(
    val segment: Segment
)

data class Segment(
    val name: String
)