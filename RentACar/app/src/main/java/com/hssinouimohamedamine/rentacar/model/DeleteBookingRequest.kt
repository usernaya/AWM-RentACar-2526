package com.hssinouimohamedamine.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Body voor delete_booking.php. googleId dient als eigenaar-check op de server.
@Serializable
data class DeleteBookingRequest(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("google_id") val googleId: String
)
