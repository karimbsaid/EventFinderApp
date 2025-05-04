package com.example.eventfinder.model

data class EventResponse(
    val _embedded: EmbeddedEvents?
)

data class EmbeddedEvents(
    val events: List<Event>
)

data class Event(
    val id: String,
    val name: String,
    val url: String, // Added for Ticketmaster redirect
    val dates: EventDates,
    val _embedded: EventEmbedded,
    val images: List<EventImage>,
    val classifications: List<Classification>? // Added for category
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
    val location: Location?,
    val city: City?, // Added for address display
    val state: State? // Added for address display
)

data class Address(
    val line1: String?
)

data class Location(
    val latitude: String?,
    val longitude: String?
)

data class City(
    val name: String?
)

data class State(
    val name: String?
)

data class Classification(
    val segment: Segment
)

data class Segment(
    val name: String
)