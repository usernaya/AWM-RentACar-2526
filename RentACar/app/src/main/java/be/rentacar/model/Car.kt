package be.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Auto-data uit get_cars.php. price/lat/long zijn Strings in de JSON.
@Serializable
data class Car(
    @SerialName("car_id") val carId: Int,
    @SerialName("agency_id") val agencyId: Int,
    val brand: String,
    val model: String,
    val type: String,
    val transmission: String,
    @SerialName("price_per_day") val pricePerDay: String,
    @SerialName("image_path") val imagePath: String,
    @SerialName("city_name") val cityName: String,
    val latitude: String,
    val longitude: String
)
