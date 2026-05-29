package be.rentacar.ui.mybookings

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.rentacar.R
import be.rentacar.ui.mybookings.components.BookingCard

// scherm Mijn reservaties
@Composable
fun MyBookingsScreen(
    onEditBooking: (carId: Int, bookingId: Int) -> Unit,
    viewModel: MyBookingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MyBookingsScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.refresh() },
        onDelete = { viewModel.deleteBooking(it) },
        onEditBooking = onEditBooking
    )
}

@Composable
private fun MyBookingsScreenContent(
    uiState: MyBookingsUiState,
    onRefresh: () -> Unit,
    onDelete: (Int) -> Unit,
    onEditBooking: (carId: Int, bookingId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // bovenbalk + refresh
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.screen_my_bookings),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRefresh) {
                Text(
                    text = stringResource(R.string.action_refresh_symbol),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        when (uiState) {
            is MyBookingsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_extra_large)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.action_loading),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is MyBookingsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_extra_large)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
                    Button(onClick = onRefresh) {
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }

            is MyBookingsUiState.Success -> {
                if (uiState.bookings.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_extra_large)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.mybookings_empty_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                        Text(
                            text = stringResource(R.string.mybookings_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                        verticalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.padding_small) + 4.dp
                        ),
                        contentPadding = PaddingValues(
                            bottom = dimensionResource(R.dimen.padding_medium)
                        )
                    ) {
                        item {
                            Text(
                                text = stringResource(
                                    R.string.mybookings_header,
                                    uiState.bookings.size
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(R.dimen.padding_small)
                                )
                            )
                        }
                        items(items = uiState.bookings, key = { it.bookingId }) { booking ->
                            BookingCard(
                                booking = booking,
                                onDeleteConfirmed = { onDelete(booking.bookingId) },
                                onEditClicked = {
                                    // carId kan null zijn (PHP JOIN)
                                    if (booking.carId != null) {
                                        onEditBooking(booking.carId!!, booking.bookingId)
                                    } else {
                                        android.util.Log.w(
                                            "MyBookingsScreen",
                                            "carId null voor booking #${booking.bookingId}"
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

