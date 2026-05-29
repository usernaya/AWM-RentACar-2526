package be.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Response voor create/update/delete_booking. Succes: {success, booking_id}, fout: {error}.
// booking_id is String (MySQL teruggeeft) — conversie via toIntOrNull() in de VM.
@Serializable
data class BookingResponse(
    val success: Boolean? = null,
    @SerialName("booking_id") val bookingId: String? = null,
    val error: String? = null
)
