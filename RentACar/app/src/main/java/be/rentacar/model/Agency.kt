package be.rentacar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Agency(
    @SerialName("agency_id") val agencyId: Int,
    @SerialName("company_name") val companyName: String = "Rent-a-Car",
    @SerialName("city_name") val cityName: String,
    val country: String,
    val latitude: String,
    val longitude: String
)
