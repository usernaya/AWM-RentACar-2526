package be.rentacar.ui.locations

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.rentacar.R
import be.rentacar.model.Agency
import be.rentacar.ui.locations.components.AgencyCard

// lijst van alle agentschappen
@Composable
fun LocationsScreen(
    viewModel: LocationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LocationsScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.refresh() },
        onMapClick = { agency -> openInGoogleMaps(context, agency) }
    )
}

@Composable
private fun LocationsScreenContent(
    uiState: LocationsUiState,
    onRefresh: () -> Unit,
    onMapClick: (Agency) -> Unit
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
                text = stringResource(R.string.screen_locations),
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
            is LocationsUiState.Loading -> {
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
            is LocationsUiState.Error -> {
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
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                    Button(onClick = onRefresh) {
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }
            is LocationsUiState.Success -> {
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
                                R.string.locations_header,
                                uiState.agencies.size
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(
                                vertical = dimensionResource(R.dimen.padding_small)
                            )
                        )
                    }
                    items(items = uiState.agencies, key = { it.agencyId }) { agency ->
                        AgencyCard(
                            agency = agency,
                            onMapClick = { onMapClick(agency) }
                        )
                    }
                }
            }
        }
    }
}

// opent Google Maps op de coordinaten, anders gewoon in de browser
private fun openInGoogleMaps(context: Context, agency: Agency) {
    val gmmIntentUri = Uri.parse(
        "geo:${agency.latitude},${agency.longitude}" +
            "?q=${Uri.encode("${agency.cityName}, ${agency.country}")}"
    )
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    try {
        context.startActivity(mapIntent)
    } catch (e: ActivityNotFoundException) {
        // Maps niet geinstalleerd - browser openen
        val webUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1" +
                "&query=${agency.latitude},${agency.longitude}"
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}

