package com.hssinouimohamedamine.rentacar.ui.mybookings

import com.hssinouimohamedamine.rentacar.model.Booking

// 3 mogelijke toestanden van het scherm
sealed class MyBookingsUiState {
    object Loading : MyBookingsUiState()
    data class Success(val bookings: List<Booking>) : MyBookingsUiState()
    data class Error(val message: String) : MyBookingsUiState()
}
