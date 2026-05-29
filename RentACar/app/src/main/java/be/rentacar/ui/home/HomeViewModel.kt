package be.rentacar.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.rentacar.network.RentACarApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "HomeViewModel"

// laadt de auto's, filters worden in de UI toegepast
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCars()
    }

    private fun loadCars() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val cars = RentACarApi.retroFitService.getCars()
                // lijst van unieke steden voor het filter
                val cities = cars.map { it.cityName }.distinct().sorted()
                _uiState.value = HomeUiState.Success(
                    cars = cars,
                    cities = cities,
                    selectedCity = null
                )
                Log.d(TAG, "Loaded ${cars.size} cars in ${cities.size} cities")
            } catch (e: IOException) {
                Log.e(TAG, "Network error: ${e.message}")
                _uiState.value = HomeUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error: ${e.code()} ${e.message()}")
                _uiState.value = HomeUiState.Error("Server fout: ${e.code()}")
            }
        }
    }

    fun selectCity(city: String?) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(selectedCity = city)
        }
    }

    fun selectFuelType(type: String?) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(selectedFuelType = type)
        }
    }

    fun selectTransmission(transmission: String?) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(selectedTransmission = transmission)
        }
    }

    fun setSortOrder(order: SortOrder) {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(sortBy = order)
        }
    }

    // reset alle filters tegelijk
    fun clearFilters() {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(
                selectedCity = null,
                selectedFuelType = null,
                selectedTransmission = null,
                sortBy = SortOrder.None
            )
        }
    }

    fun refresh() = loadCars()
}
