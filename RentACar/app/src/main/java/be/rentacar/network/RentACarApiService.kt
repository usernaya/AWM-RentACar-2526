package be.rentacar.network

import be.rentacar.model.Agency
import be.rentacar.model.Booking
import be.rentacar.model.BookingRequest
import be.rentacar.model.BookingResponse
import be.rentacar.model.Car
import be.rentacar.model.DeleteBookingRequest
import be.rentacar.model.UpdateBookingRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// de 6 endpoints van mijn PHP backend
interface RentACarApiService {

    @GET("get_agencies.php")
    suspend fun getAgencies(): List<Agency>

    @GET("get_cars.php")
    suspend fun getCars(): List<Car>

    @GET("get_my_bookings.php")
    suspend fun getMyBookings(
        @Query("google_id") googleId: String
    ): List<Booking>

    @POST("create_booking.php")
    suspend fun createBooking(@Body request: BookingRequest): BookingResponse

    @POST("update_booking.php")
    suspend fun updateBooking(@Body request: UpdateBookingRequest): BookingResponse

    @POST("delete_booking.php")
    suspend fun deleteBooking(@Body request: DeleteBookingRequest): BookingResponse
}
