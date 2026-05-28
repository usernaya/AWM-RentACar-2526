package com.hssinouimohamedamine.rentacar.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.auth.AuthManager
import com.hssinouimohamedamine.rentacar.model.Car
import com.hssinouimohamedamine.rentacar.ui.home.components.CarCard
import com.hssinouimohamedamine.rentacar.ui.theme.RentACarTheme

// haalt de state op uit het ViewModel en geeft events door
@Composable
fun HomeScreen(
    onCarClick: (carId: Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val defaultUserName = stringResource(R.string.home_default_username)
    val userName = AuthManager.currentUser
        ?.displayName
        ?.split(" ")
        ?.firstOrNull()
        ?: defaultUserName

    HomeScreenContent(
        uiState = uiState,
        userName = userName,
        onRefresh = { viewModel.refresh() },
        onSelectCity = { viewModel.selectCity(it) },
        onSelectFuel = { viewModel.selectFuelType(it) },
        onSelectTransmission = { viewModel.selectTransmission(it) },
        onSetSort = { viewModel.setSortOrder(it) },
        onClearFilters = { viewModel.clearFilters() },
        onCarClick = onCarClick
    )
}

// los van het ViewModel zodat ik een preview kan maken
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    userName: String,
    onRefresh: () -> Unit,
    onSelectCity: (String?) -> Unit,
    onSelectFuel: (String?) -> Unit,
    onSelectTransmission: (String?) -> Unit,
    onSetSort: (SortOrder) -> Unit,
    onClearFilters: () -> Unit,
    onCarClick: (carId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeTopBar(
            userName = userName,
            onRefresh = onRefresh
        )

        when (uiState) {
            is HomeUiState.Loading -> LoadingContent()
            is HomeUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRefresh
            )
            is HomeUiState.Success -> SuccessContent(
                state = uiState,
                onSelectCity = onSelectCity,
                onSelectFuel = onSelectFuel,
                onSelectTransmission = onSelectTransmission,
                onSetSort = onSetSort,
                onClearFilters = onClearFilters,
                onCarClick = onCarClick
            )
        }
    }
}

// eigen topbalk met begroeting + refresh, compacter in landscape
@Composable
private fun HomeTopBar(
    userName: String,
    onRefresh: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation ==
        Configuration.ORIENTATION_LANDSCAPE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = if (isLandscape) {
                    dimensionResource(R.dimen.padding_tiny)
                } else {
                    dimensionResource(R.dimen.padding_medium)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_greeting, userName),
                style = if (isLandscape) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.headlineMedium
                },
                color = MaterialTheme.colorScheme.primary
            )
            // subtitel enkel tonen in portrait
            if (!isLandscape) {
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
        TextButton(onClick = onRefresh) {
            Text(
                text = "↻",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.action_loading),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_extra_large)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}

// filters + lijst auto's, filters scrollen mee zodat de lijst groter blijft
@Composable
private fun SuccessContent(
    state: HomeUiState.Success,
    onSelectCity: (String?) -> Unit,
    onSelectFuel: (String?) -> Unit,
    onSelectTransmission: (String?) -> Unit,
    onSetSort: (SortOrder) -> Unit,
    onClearFilters: () -> Unit,
    onCarClick: (carId: Int) -> Unit
) {
    // filteren + sorteren in de UI
    val filteredCars = state.cars
        .filter { car ->
            val cityOk = state.selectedCity == null ||
                car.cityName == state.selectedCity
            val fuelOk = state.selectedFuelType == null ||
                car.type == state.selectedFuelType
            val transOk = state.selectedTransmission == null ||
                car.transmission == state.selectedTransmission
            cityOk && fuelOk && transOk
        }
        .let { list ->
            when (state.sortBy) {
                SortOrder.PriceAsc -> list.sortedBy {
                    it.pricePerDay.toFloatOrNull() ?: 0f
                }
                SortOrder.PriceDesc -> list.sortedByDescending {
                    it.pricePerDay.toFloatOrNull() ?: 0f
                }
                SortOrder.None -> list
            }
        }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // filters scrollen mee, volle breedte
        item(span = { GridItemSpan(maxLineSpan) }) {
            FiltersSection(
                state = state,
                onSelectCity = onSelectCity,
                onSelectFuel = onSelectFuel,
                onSelectTransmission = onSelectTransmission,
                onSetSort = onSetSort,
                onClearFilters = onClearFilters
            )
        }

        if (filteredCars.isEmpty() && state.cars.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FilterEmptyState(onClearFilters = onClearFilters)
            }
        } else {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.home_available_cars, filteredCars.size),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_tiny))
                )
            }
            items(items = filteredCars, key = { it.carId }) { car ->
                CarCard(
                    car = car,
                    onClick = { onCarClick(car.carId) }
                )
            }
        }
    }
}

// filters: chips voor stad/brandstof/transmissie + sorteren + wissen
@Composable
private fun FiltersSection(
    state: HomeUiState.Success,
    onSelectCity: (String?) -> Unit,
    onSelectFuel: (String?) -> Unit,
    onSelectTransmission: (String?) -> Unit,
    onSetSort: (SortOrder) -> Unit,
    onClearFilters: () -> Unit
) {
    val anyFilterActive = state.selectedCity != null ||
        state.selectedFuelType != null ||
        state.selectedTransmission != null ||
        state.sortBy != SortOrder.None

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.padding_medium),
                vertical = dimensionResource(R.dimen.padding_small)
            )
    ) {
        // steden
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.padding_tiny))
        ) {
            item {
                CityChip(
                    label = stringResource(R.string.home_filter_all_cities),
                    selected = state.selectedCity == null,
                    onClick = { onSelectCity(null) }
                )
            }
            items(items = state.cities, key = { it }) { city ->
                CityChip(
                    label = city,
                    selected = state.selectedCity == city,
                    onClick = { onSelectCity(city) }
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

        // brandstof + sort + wissen
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            SortDropdownButton(
                currentSort = state.sortBy,
                onSetSort = onSetSort
            )
            if (anyFilterActive) {
                TextButton(onClick = onClearFilters) {
                    Text(
                        text = stringResource(R.string.filter_clear),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        FuelFilterRow(
            selectedFuel = state.selectedFuelType,
            onSelectFuel = onSelectFuel
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

        // transmissie
        TransmissionFilterRow(
            selectedTransmission = state.selectedTransmission,
            onSelectTransmission = onSelectTransmission
        )
    }
}

// chips voor brandstoftype, tweede klik op dezelfde chip = uitzetten
@Composable
private fun FuelFilterRow(
    selectedFuel: String?,
    onSelectFuel: (String?) -> Unit
) {
    val fuels = listOf(
        "benzine" to R.string.filter_fuel_benzine,
        "elektrisch" to R.string.filter_fuel_elektrisch,
        "hybride" to R.string.filter_fuel_hybride
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.padding_tiny))
    ) {
        items(items = fuels, key = { it.first }) { (dbValue, labelRes) ->
            val isSelected = selectedFuel == dbValue
            FilterChip(
                selected = isSelected,
                onClick = {
                    onSelectFuel(if (isSelected) null else dbValue)
                },
                label = { Text(stringResource(labelRes)) }
            )
        }
    }
}

// idem maar voor transmissie
@Composable
private fun TransmissionFilterRow(
    selectedTransmission: String?,
    onSelectTransmission: (String?) -> Unit
) {
    val transmissions = listOf(
        "handgeschakeld" to R.string.filter_transmission_handgeschakeld,
        "automatisch" to R.string.filter_transmission_automatisch
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.padding_tiny))
    ) {
        items(items = transmissions, key = { it.first }) { (dbValue, labelRes) ->
            val isSelected = selectedTransmission == dbValue
            FilterChip(
                selected = isSelected,
                onClick = {
                    onSelectTransmission(if (isSelected) null else dbValue)
                },
                label = { Text(stringResource(labelRes)) }
            )
        }
    }
}

// sorteer-knop met dropdown
@Composable
private fun SortDropdownButton(
    currentSort: SortOrder,
    onSetSort: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Text(
                text = "⇅",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortMenuItem(
                labelRes = R.string.filter_sort_none,
                isCurrent = currentSort == SortOrder.None,
                onClick = {
                    onSetSort(SortOrder.None)
                    expanded = false
                }
            )
            SortMenuItem(
                labelRes = R.string.filter_sort_price_asc,
                isCurrent = currentSort == SortOrder.PriceAsc,
                onClick = {
                    onSetSort(SortOrder.PriceAsc)
                    expanded = false
                }
            )
            SortMenuItem(
                labelRes = R.string.filter_sort_price_desc,
                isCurrent = currentSort == SortOrder.PriceDesc,
                onClick = {
                    onSetSort(SortOrder.PriceDesc)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun SortMenuItem(
    labelRes: Int,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(labelRes),
                color = if (isCurrent) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        },
        onClick = onClick
    )
}

// als de filters geen resultaten geven
@Composable
private fun FilterEmptyState(onClearFilters: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_extra_large)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            fontSize = 60.sp
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Text(
            text = stringResource(R.string.filter_empty_state),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        Button(onClick = onClearFilters) {
            Text(text = stringResource(R.string.filter_clear))
        }
    }
}

// custom chip met Button
@Composable
private fun CityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }
    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium)),
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.status_badge_padding_h),
            vertical = dimensionResource(R.dimen.status_badge_padding_v)
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

// preview

private val previewCar = Car(
    carId = 1,
    agencyId = 1,
    brand = "BMW",
    model = "X5",
    type = "benzine",
    transmission = "automatisch",
    pricePerDay = "85.00",
    imagePath = "bmw_x5.jpg",
    cityName = "Madrid",
    latitude = "40.41",
    longitude = "-3.70"
)

@Preview(showBackground = true, name = "HomeScreen — Success met filter")
@Composable
private fun HomeScreenSuccessPreview() {
    RentACarTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                cars = listOf(previewCar),
                cities = listOf("Brussel", "Madrid", "Parijs"),
                selectedCity = "Madrid"
            ),
            userName = "Mohamed",
            onRefresh = {},
            onSelectCity = {},
            onSelectFuel = {},
            onSelectTransmission = {},
            onSetSort = {},
            onClearFilters = {},
            onCarClick = {}
        )
    }
}

