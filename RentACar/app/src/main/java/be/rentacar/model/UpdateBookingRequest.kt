package be.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Body voor update_booking.php — geen user-data nodig, we identificeren via bookingId.
@Serializable
data class UpdateBookingRequest(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("car_id") val carId: Int,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("total_price") val totalPrice: String
)
