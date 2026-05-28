package com.hssinouimohamedamine.rentacar.model

import com.hssinouimohamedamine.rentacar.util.daysBetween

// duurtijd van een booking in dagen
val Booking.durationDays: Int?
    get() = daysBetween(startDate, endDate)
