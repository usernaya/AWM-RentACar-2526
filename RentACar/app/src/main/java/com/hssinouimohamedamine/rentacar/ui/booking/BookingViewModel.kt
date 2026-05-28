package com.hssinouimohamedamine.rentacar.ui.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hssinouimohamedamine.rentacar.auth.AuthManager
import com.hssinouimohamedamine.rentacar.model.BookingRequest
import com.hssinouimohamedamine.rentacar.model.Car
import com.hssinouimohamedamine.rentacar.model.UpdateBookingRequest
import com.hssinouimohamedamine.rentacar.network.RentACarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// laadt de auto + verstuurt nieuwe of gewijzigde reservatie
class BookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BookingUiState>(BookingUiState.Loading)
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private var loadedCar: Car? = null

    // null = nieuw, anders = wijzigen
    private var editingBookingId: Int? = null

    fun loadCar(carId: Int) {
        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            try {
                val cars = RentACarApi.retroFitService.getCars()
                val car = cars.find { it.carId == carId }
                if (car != null) {
                    loadedCar = car
                    _uiState.value = BookingUiState.FormReady(car)
                    Log.d(TAG, "Car loaded: ${car.brand} ${car.model}")
                } else {
                    _uiState.value = BookingUiState.Error("Auto niet gevonden")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                _uiState.value = BookingUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error: ${e.code()}", e)
                _uiState.value = BookingUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                // bv. als PHP een HTML error-pagina teruggeeft i.p.v. JSON
                Log.e(TAG, "Unexpected error during loadCar", e)
                _uiState.value = BookingUiState.Error("Server fout: kon antwoord niet verwerken")
            }
        }
    }

    fun submitBooking(startDate: String, endDate: String, totalPrice: String) {
        val car = loadedCar ?: return
        val firebaseUser = AuthManager.currentUser
        if (firebaseUser == null) {
            _uiState.value = BookingUiState.Error("Niet ingelogd")
            return
        }
        val googleId = firebaseUser.uid
        val email = firebaseUser.email ?: ""
        val fullName = firebaseUser.displayName ?: "Onbekend"

        viewModelScope.launch {
            _uiState.value = BookingUiState.Submitting
            try {
                val request = BookingRequest(
                    googleId = googleId,
                    carId = car.carId,
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = totalPrice,
                    email = email,
                    fullName = fullName
                )
                Log.d(TAG, "==== SENDING REQUEST ====")
                Log.d(TAG, "googleId: $googleId")
                Log.d(TAG, "carId: ${car.carId}")
                Log.d(TAG, "email: $email")
                Log.d(TAG, "fullName: $fullName")
                Log.d(TAG, "==========================")
                val response = RentACarApi.retroFitService.createBooking(request)
                if (response.success == true) {
                    val bookingIdInt = response.bookingId?.toIntOrNull() ?: 0
                    _uiState.value = BookingUiState.Success(bookingIdInt)
                    Log.d(TAG, "Booking created: ${response.bookingId}")
                } else {
                    val errorMsg = response.error ?: "Onbekende fout bij reservatie"
                    _uiState.value = BookingUiState.Error(errorMsg)
                    Log.e(TAG, "Booking failed: $errorMsg")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error during submit", e)
                _uiState.value = BookingUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error during submit: ${e.code()}", e)
                _uiState.value = BookingUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during submit", e)
                _uiState.value = BookingUiState.Error(
                    "Server fout: kon antwoord niet verwerken. Reservatie mogelijk wel gelukt — controleer Mijn reservaties."
                )
            }
        }
    }

    // bij wijzigen: auto + bestaande booking ophalen zodat de datums al ingevuld staan
    fun loadCarForEdit(carId: Int, bookingId: Int) {
        editingBookingId = bookingId
        val googleId = AuthManager.currentUser?.uid

        viewModelScope.launch {
            _uiState.value = BookingUiState.Loading
            try {
                val cars = RentACarApi.retroFitService.getCars()
                val car = cars.find { it.carId == carId }

                // datums enkel pre-fillen als de user ingelogd is
                val existingBooking = if (googleId != null) {
                    RentACarApi.retroFitService.getMyBookings(googleId)
                        .find { it.bookingId == bookingId }
                } else null

                if (car != null) {
                    loadedCar = car
                    _uiState.value = BookingUiState.FormReady(
                        car = car,
                        prefilledStartDate = existingBooking?.startDate,
                        prefilledEndDate = existingBooking?.endDate
                    )
                    Log.d(TAG, "Edit mode loaded — car=${car.brand}, dates=${existingBooking?.startDate}→${existingBooking?.endDate}")
                } else {
                    _uiState.value = BookingUiState.Error("Auto niet gevonden")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                _uiState.value = BookingUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error: ${e.code()}", e)
                _uiState.value = BookingUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during loadCarForEdit", e)
                _uiState.value = BookingUiState.Error("Server fout: kon antwoord niet verwerken")
            }
        }
    }

    // POST naar update_booking.php — loadCarForEdit() moet eerst gebeurd zijn
    fun updateBooking(startDate: String, endDate: String, totalPrice: String) {
        val car = loadedCar ?: return
        val bookingId = editingBookingId ?: return

        viewModelScope.launch {
            _uiState.value = BookingUiState.Submitting
            try {
                val request = UpdateBookingRequest(
                    bookingId = bookingId,
                    carId = car.carId,
                    startDate = startDate,
                    endDate = endDate,
                    totalPrice = totalPrice
                )
                Log.d(TAG, "==== UPDATING BOOKING #$bookingId ====")
                Log.d(TAG, "carId: ${car.carId}")
                Log.d(TAG, "period: $startDate -> $endDate")
                Log.d(TAG, "totalPrice: $totalPrice")
                Log.d(TAG, "=======================================")
                val response = RentACarApi.retroFitService.updateBooking(request)
                if (response.success == true) {
                    _uiState.value = BookingUiState.Success(bookingId)
                    Log.d(TAG, "Booking updated: #$bookingId")
                } else {
                    val errorMsg = response.error ?: "Onbekende fout bij wijzigen"
                    _uiState.value = BookingUiState.Error(errorMsg)
                    Log.e(TAG, "Update failed: $errorMsg")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error during update", e)
                _uiState.value = BookingUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error during update: ${e.code()}", e)
                _uiState.value = BookingUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during update", e)
                _uiState.value = BookingUiState.Error(
                    "Server fout: kon antwoord niet verwerken. Wijziging mogelijk wel gelukt — controleer Mijn reservaties."
                )
            }
        }
    }

    companion object {
        private const val TAG = "BookingViewModel"
    }
}
