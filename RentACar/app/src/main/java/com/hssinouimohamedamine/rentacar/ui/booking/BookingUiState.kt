package com.hssinouimohamedamine.rentacar.ui.booking

import com.hssinouimohamedamine.rentacar.model.Car

// 5 mogelijke toestanden van het scherm
sealed class BookingUiState {
    object Loading : BookingUiState()

    // bij wijzigen zijn de prefilled datums ingevuld
    data class FormReady(
        val car: Car,
        val prefilledStartDate: String? = null,
        val prefilledEndDate: String? = null
    ) : BookingUiState()

    object Submitting : BookingUiState()
    data class Success(val bookingId: Int) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}
