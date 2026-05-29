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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.hssinouimohamedamine.rentacar.util.millisToYyyyMmDd
import com.hssinouimohamedamine.rentacar.util.parseDateOrNull
import com.hssinouimohamedamine.rentacar.util.toMillisOrNull
import com.hssinouimohamedamine.rentacar.util.toReadableDate

// "vandaag" voor de demo
private const val TODAY = "2026-05-12"

@Composable
fun BookingScreen(
    carId: Int,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    bookingIdToEdit: Int? = null,
    viewModel: BookingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = bookingIdToEdit != null

    // opnieuw laden als je naar een andere auto gaat
    LaunchedEffect(carId, bookingIdToEdit) {
        if (bookingIdToEdit != null) {
            viewModel.loadCarForEdit(carId, bookingIdToEdit)
        } else {
            viewModel.loadCar(carId)
        }
    }

    BookingScreenContent(
        uiState = uiState,
        isEditMode = isEditMode,
        onBack = onBack,
        onRetryLoad = {
            if (bookingIdToEdit != null) {
                viewModel.loadCarForEdit(carId, bookingIdToEdit)
            } else {
                viewModel.loadCar(carId)
            }
        },
        onSubmit = { s, e, p ->
            if (isEditMode) viewModel.updateBooking(s, e, p)
            else viewModel.submitBooking(s, e, p)
        },
        onBookingSuccess = onBookingSuccess
    )
}

@Composable
private fun BookingScreenContent(
    uiState: BookingUiState,
    isEditMode: Boolean,
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
                // titel verschilt voor bewerken vs nieuw
                text = stringResource(
                    if (isEditMode) R.string.booking_edit_title
                    else R.string.booking_new_title
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onBack) {
                Text(text = stringResource(R.string.action_back))
            }
        }

        when (val state = uiState) {
            is BookingUiState.Loading -> CenteredLoading()
            is BookingUiState.Error -> ErrorContent(state.message, onRetryLoad)
            is BookingUiState.FormReady -> BookingForm(
                car = state.car,
                isEditMode = isEditMode,
                prefilledStartDate = state.prefilledStartDate,
                prefilledEndDate = state.prefilledEndDate,
                onSubmit = onSubmit
            )
            is BookingUiState.Submitting -> SubmittingContent()
            is BookingUiState.Success -> SuccessContent(state.bookingId, onBookingSuccess)
        }
    }
}

@Composable
private fun BookingForm(
    car: Car,
    isEditMode: Boolean,
    prefilledStartDate: String? = null,
    prefilledEndDate: String? = null,
    onSubmit: (String, String, String) -> Unit
) {
    // bewaart de datum bij rotatie
    var startDate by rememberSaveable {
        mutableStateOf(prefilledStartDate.orEmpty())
    }
    var endDate by rememberSaveable {
        mutableStateOf(prefilledEndDate.orEmpty())
    }

    val days = daysBetween(startDate, endDate)
    val pricePerDay = car.pricePerDay.toFloatOrNull() ?: 0f
    val totalPrice = if (days != null) days * pricePerDay else 0f

    // startdatum mag niet in het verleden zijn
    val isStartInPast = startDate.isNotBlank() &&
        parseDateOrNull(startDate) != null &&
        startDate < TODAY

    val startDateValid = startDate.isBlank() ||
        (parseDateOrNull(startDate) != null && !isStartInPast)
    val endDateValid = endDate.isBlank() || parseDateOrNull(endDate) != null

    val canSubmit = days != null && days > 0 && !isStartInPast

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
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))
                Text(
                    text = car.cityName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
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
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

        // knop opent de Material 3 DatePicker
        DateButton(
            placeholderLabel = stringResource(R.string.booking_pick_start_date),
            selectedDate = startDate,
            isError = !startDateValid,
            onDateSelected = { startDate = it }
        )
        if (isStartInPast) {
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))
            Text(
                text = stringResource(R.string.booking_date_in_past),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))
        DateButton(
            placeholderLabel = stringResource(R.string.booking_pick_end_date),
            selectedDate = endDate,
            isError = !endDateValid,
            onDateSelected = { endDate = it }
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

        if (canSubmit) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                    Text(
                        text = stringResource(R.string.booking_days_count, days ?: 0),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))
                    Text(
                        text = stringResource(
                            R.string.booking_total_price,
                            formatEuros(totalPrice)
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = PriceGreen
                    )
                }
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
        } else if (startDate.isNotBlank() && endDate.isNotBlank()) {
            Text(
                text = stringResource(R.string.booking_invalid_dates),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
        }

        Button(
            onClick = { onSubmit(startDate, endDate, "%.2f".format(totalPrice)) },
            enabled = canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.button_height_large))
        ) {
            Text(
                text = stringResource(
                    if (isEditMode) R.string.booking_edit_title
                    else R.string.booking_confirm_button
                ),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))
    }
}

// knop die de DatePicker opent en de gekozen datum terug omhoog stuurt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateButton(
    placeholderLabel: String,
    selectedDate: String,
    isError: Boolean,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // lokaal state voor de dialog
    var showDialog by remember { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toMillisOrNull()
    )

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
        Text(
            text = if (selectedDate.isBlank()) placeholderLabel
            else selectedDate.toReadableDate(),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millisToYyyyMmDd(millis))
                    }
                    showDialog = false
                }) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun CenteredLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_extra_large) + 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.action_loading),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SubmittingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_extra_large) + 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.booking_submitting),
            style = MaterialTheme.typography.bodyLarge
        )
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
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large) + 16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large))
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.booking_success_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4CAF50)
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                Text(
                    text = stringResource(R.string.booking_success_number, bookingId),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
        Button(
            onClick = onOk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.booking_back_home))
        }
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
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large) + 16.dp))
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

@Preview(showBackground = true, name = "Booking — Form (Nieuwe)")
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
                    cityName = "Madrid",
                    latitude = "40.41",
                    longitude = "-3.70"
                )
            ),
            isEditMode = false,
            onBack = {},
            onRetryLoad = {},
            onSubmit = { _, _, _ -> },
            onBookingSuccess = {}
        )
    }
}
