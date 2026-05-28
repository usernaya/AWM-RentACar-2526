package com.hssinouimohamedamine.rentacar.ui.mybookings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hssinouimohamedamine.rentacar.auth.AuthManager
import com.hssinouimohamedamine.rentacar.model.DeleteBookingRequest
import com.hssinouimohamedamine.rentacar.network.RentACarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// haalt de reservaties op en kan ze verwijderen
class MyBookingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MyBookingsUiState>(MyBookingsUiState.Loading)
    val uiState: StateFlow<MyBookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        val googleId = AuthManager.currentUser?.uid
        if (googleId == null) {
            _uiState.value = MyBookingsUiState.Error("Niet ingelogd")
            return
        }

        viewModelScope.launch {
            _uiState.value = MyBookingsUiState.Loading
            try {
                Log.d(TAG, "==== FETCHING BOOKINGS ====")
                Log.d(TAG, "googleId: $googleId")
                Log.d(TAG, "===========================")
                val bookings = RentACarApi.retroFitService.getMyBookings(googleId)
                Log.d(TAG, "Loaded ${bookings.size} bookings")
                bookings.forEach { b ->
                    Log.d(
                        TAG,
                        "  -> #${b.bookingId} ${b.brand} ${b.model} " +
                            "${b.startDate}→${b.endDate} " +
                            "status=${b.status} carId=${b.carId}"
                    )
                }
                _uiState.value = MyBookingsUiState.Success(bookings)
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                _uiState.value = MyBookingsUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error: ${e.code()}", e)
                _uiState.value = MyBookingsUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during loadBookings", e)
                _uiState.value = MyBookingsUiState.Error("Server fout: kon antwoord niet verwerken")
            }
        }
    }

    fun deleteBooking(bookingId: Int) {
        val googleId = AuthManager.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val request = DeleteBookingRequest(googleId = googleId, bookingId = bookingId)
                val response = RentACarApi.retroFitService.deleteBooking(request)
                if (response.success == true) {
                    Log.d(TAG, "Booking $bookingId deleted")
                    loadBookings()
                } else {
                    val errorMsg = response.error ?: "Verwijderen mislukt"
                    Log.e(TAG, "Delete failed: $errorMsg")
                    _uiState.value = MyBookingsUiState.Error(errorMsg)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error during delete", e)
                _uiState.value = MyBookingsUiState.Error("Geen netwerkverbinding bij verwijderen")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error during delete: ${e.code()}", e)
                _uiState.value = MyBookingsUiState.Error("Server fout: ${e.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during delete", e)
                _uiState.value = MyBookingsUiState.Error("Server fout: kon antwoord niet verwerken")
            }
        }
    }

    fun refresh() = loadBookings()

    companion object {
        private const val TAG = "MyBookingsViewModel"
    }
}
