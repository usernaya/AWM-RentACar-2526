package be.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Body voor create_booking.php. email + full_name nodig omdat PHP een nieuwe user kan aanmaken.
@Serializable
data class BookingRequest(
    @SerialName("google_id") val googleId: String,
    @SerialName("car_id") val carId: Int,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("total_price") val totalPrice: String,
    val email: String,
    @SerialName("full_name") val fullName: String
)
