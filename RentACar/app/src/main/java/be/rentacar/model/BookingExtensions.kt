package be.rentacar.model

import be.rentacar.util.daysBetween

// duurtijd van een booking in dagen
val Booking.durationDays: Int?
    get() = daysBetween(startDate, endDate)
