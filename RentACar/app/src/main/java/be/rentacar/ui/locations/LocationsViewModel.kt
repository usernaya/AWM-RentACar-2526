package be.rentacar.ui.locations

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

private const val TAG = "LocationsViewModel"

// haalt de agentschappen op bij het openen
class LocationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LocationsUiState>(LocationsUiState.Loading)
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadAgencies()
    }

    private fun loadAgencies() {
        viewModelScope.launch {
            _uiState.value = LocationsUiState.Loading
            try {
                val agencies = RentACarApi.retroFitService.getAgencies()
                _uiState.value = LocationsUiState.Success(agencies)
                Log.d(TAG, "Loaded ${agencies.size} agencies")
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                _uiState.value = LocationsUiState.Error("Geen netwerkverbinding")
            } catch (e: HttpException) {
                Log.e(TAG, "HTTP error", e)
                _uiState.value = LocationsUiState.Error("Server fout: ${e.code()}")
            }
        }
    }

    fun refresh() = loadAgencies()
}
