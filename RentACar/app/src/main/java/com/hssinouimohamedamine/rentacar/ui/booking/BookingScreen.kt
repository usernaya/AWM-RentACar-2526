package com.hssinouimohamedamine.rentacar.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.model.Car
import com.hssinouimohamedamine.rentacar.ui.components.CarVisual
import com.hssinouimohamedamine.rentacar.ui.theme.PriceGreen
import com.hssinouimohamedamine.rentacar.ui.theme.RentACarTheme
import com.hssinouimohamedamine.rentacar.util.daysBetween
import com.hssinouimohamedamine.rentacar.util.formatEuros
import com.hssinouimohamedamine.rentacar.util.formatEurosFromString

@Composable
fun BookingScreen(
    carId: Int,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(carId) {
        viewModel.loadCar(carId)
    }

    BookingScreenContent(
        uiState = uiState,
        onBack = onBack,
        onRetryLoad = { viewModel.loadCar(carId) },
        onSubmit = { startDate, endDate, totalPrice ->
            viewModel.submitBooking(startDate, endDate, totalPrice)
        },
        onBookingSuccess = onBookingSuccess
    )
}

@Composable
private fun BookingScreenContent(
    uiState: BookingUiState,
    onBack: () -> Unit,
    onRetryLoad: () -> Unit,
    onSubmit: (String, String, String) -> Unit,
    onBookingSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.booking_new_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        when (val state = uiState) {
            is BookingUiState.Loading -> CenteredMessage(stringResource(R.string.action_loading))
            is BookingUiState.Error -> ErrorContent(state.message, onRetryLoad)
            is BookingUiState.FormReady -> BookingForm(
                car = state.car,
                onSubmit = onSubmit
            )
            is BookingUiState.Submitting -> CenteredMessage(stringResource(R.string.booking_submitting))
            is BookingUiState.Success -> SuccessContent(state.bookingId, onBookingSuccess)
        }
    }
}

@Composable
private fun BookingForm(
    car: Car,
    onSubmit: (String, String, String) -> Unit
) {
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }

    val days = daysBetween(startDate, endDate)
    val pricePerDay = car.pricePerDay.toFloatOrNull() ?: 0f
    val totalPrice = if (days != null && days > 0) days * pricePerDay else 0f
    val canSubmit = startDate.isNotBlank() && endDate.isNotBlank()

    Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                CarVisual(
                    brand = car.brand,
                    imagePath = car.imagePath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.image_car_form))
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = car.cityName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(
                        R.string.booking_price_per_day,
                        formatEurosFromString(car.pricePerDay)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = PriceGreen
                )
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

        Text(
            text = stringResource(R.string.booking_choose_period),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text(stringResource(R.string.booking_start_date_label)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text(stringResource(R.string.booking_end_date_label)) },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))

        Text(
            text = stringResource(
                R.string.booking_total_price,
                formatEuros(totalPrice)
            ),
            style = MaterialTheme.typography.titleMedium,
            color = PriceGreen
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))

        Button(
            onClick = { onSubmit(startDate, endDate, "%.2f".format(totalPrice)) },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.button_height_large))
        ) {
            Text(text = stringResource(R.string.booking_confirm_button))
        }
    }
}

@Composable
private fun CenteredMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_extra_large) + 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_extra_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.action_retry))
        }
    }
}

@Composable
private fun SuccessContent(bookingId: Int, onOk: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_extra_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.booking_success_title),
            style = MaterialTheme.typography.headlineSmall,
            color = PriceGreen
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
        Text(
            text = stringResource(R.string.booking_success_number, bookingId),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
        Button(
            onClick = onOk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.screen_my_bookings))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingFormPreview() {
    RentACarTheme {
        BookingScreenContent(
            uiState = BookingUiState.FormReady(
                Car(
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
            ),
            onBack = {},
            onRetryLoad = {},
            onSubmit = { _, _, _ -> },
            onBookingSuccess = {}
        )
    }
}
