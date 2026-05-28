package com.hssinouimohamedamine.rentacar.ui.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.auth.AuthManager
import com.hssinouimohamedamine.rentacar.model.Car
import com.hssinouimohamedamine.rentacar.ui.home.components.CarCard
import com.hssinouimohamedamine.rentacar.ui.theme.RentACarTheme

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
        onCarClick = onCarClick
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    userName: String,
    onRefresh: () -> Unit,
    onCarClick: (carId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_greeting, userName),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(onClick = onRefresh) {
                Text(text = stringResource(R.string.action_refresh_symbol))
            }
        }

        when (uiState) {
            is HomeUiState.Loading -> CenterText(stringResource(R.string.action_loading))
            is HomeUiState.Error -> ErrorContent(uiState.message, onRefresh)
            is HomeUiState.Success -> CarList(uiState.cars, onCarClick)
        }
    }
}

@Composable
private fun CarList(
    cars: List<Car>,
    onCarClick: (carId: Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.home_available_cars, cars.size),
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(items = cars, key = { it.carId }) { car ->
            CarCard(
                car = car,
                onClick = { onCarClick(car.carId) }
            )
        }
    }
}

@Composable
private fun CenterText(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
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

private val previewCar = Car(
    carId = 1,
    agencyId = 1,
    brand = "BMW",
    model = "X5",
    type = "benzine",
    transmission = "automatisch",
    pricePerDay = "85.00",
    imagePath = "bmw_x5.jpg",
    cityName = "Brussel",
    latitude = "50.85",
    longitude = "4.35"
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    RentACarTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                cars = listOf(previewCar),
                cities = emptyList()
            ),
            userName = "Mohamed",
            onRefresh = {},
            onCarClick = {}
        )
    }
}
