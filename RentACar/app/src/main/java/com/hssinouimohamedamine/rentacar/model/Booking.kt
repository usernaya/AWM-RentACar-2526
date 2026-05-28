package com.hssinouimohamedamine.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Reservering uit get_my_bookings.php (JOIN bookings + users + cars + agencies).
// userId/carId nullable omdat de JOIN ze niet altijd teruggeeft.
@Serializable
data class Booking(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("total_price") val totalPrice: String,
    val status: String? = null,
    val brand: String,
    val model: String,
    @SerialName("image_path") val imagePath: String,
    @SerialName("city_name") val cityName: String,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("car_id") val carId: Int? = null
)
