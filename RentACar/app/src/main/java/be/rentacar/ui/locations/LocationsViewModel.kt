package be.rentacar.ui.locations

import android.util.Log
import androidx.lifecycle.ViewModel
import be.rentacar.data.LocalAgencyData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "LocationsViewModel"

// haalt de lokale agentschappen op bij het openen
class LocationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LocationsUiState>(LocationsUiState.Loading)
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    init {
        loadAgencies()
    }

    private fun loadAgencies() {
        val agencies = LocalAgencyData.agencies
        _uiState.value = LocationsUiState.Success(agencies)
        Log.d(TAG, "Loaded ${agencies.size} local agencies")
    }

    fun refresh() = loadAgencies()
}
