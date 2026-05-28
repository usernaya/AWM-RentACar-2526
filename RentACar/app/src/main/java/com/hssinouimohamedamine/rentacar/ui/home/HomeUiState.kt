package com.hssinouimohamedamine.rentacar.ui.home

import com.hssinouimohamedamine.rentacar.model.Car

// sorteer-opties voor de auto-lijst
enum class SortOrder {
    None,
    PriceAsc,
    PriceDesc
}

// 3 mogelijke toestanden van het scherm
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val cars: List<Car>,
        val cities: List<String>,
        val selectedCity: String? = null,
        val selectedFuelType: String? = null,
        val selectedTransmission: String? = null,
        val sortBy: SortOrder = SortOrder.None
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
