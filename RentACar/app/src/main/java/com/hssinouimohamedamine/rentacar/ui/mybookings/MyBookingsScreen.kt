package com.hssinouimohamedamine.rentacar.ui.mybookings

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
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.ui.mybookings.components.BookingCard

@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MyBookingsScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.refresh() }
    )
}

@Composable
private fun MyBookingsScreenContent(
    uiState: MyBookingsUiState,
    onRefresh: () -> Unit
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
            Text(
                text = stringResource(R.string.screen_my_bookings),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRefresh) {
                Text(text = stringResource(R.string.action_refresh_symbol))
            }
        }

        when (uiState) {
            is MyBookingsUiState.Loading -> CenterText(stringResource(R.string.action_loading))
            is MyBookingsUiState.Error -> ErrorContent(uiState.message, onRefresh)
            is MyBookingsUiState.Success -> {
                if (uiState.bookings.isEmpty()) {
                    EmptyContent()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
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
                            BookingCard(booking = booking)
                        }
                    }
                }
            }
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
private fun EmptyContent() {
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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_extra_large)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
