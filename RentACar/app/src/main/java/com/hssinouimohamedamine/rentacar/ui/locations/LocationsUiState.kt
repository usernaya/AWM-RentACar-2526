package com.hssinouimohamedamine.rentacar.ui.locations

import com.hssinouimohamedamine.rentacar.model.Agency

// 3 mogelijke toestanden van het scherm
sealed class LocationsUiState {
    object Loading : LocationsUiState()
    data class Success(val agencies: List<Agency>) : LocationsUiState()
    data class Error(val message: String) : LocationsUiState()
}
