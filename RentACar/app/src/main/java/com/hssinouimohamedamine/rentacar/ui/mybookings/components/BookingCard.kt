package com.hssinouimohamedamine.rentacar.ui.mybookings.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.model.Booking
import com.hssinouimohamedamine.rentacar.model.durationDays
import com.hssinouimohamedamine.rentacar.ui.components.CarVisual
import com.hssinouimohamedamine.rentacar.ui.theme.PriceGreen
import com.hssinouimohamedamine.rentacar.util.formatEurosFromString
import com.hssinouimohamedamine.rentacar.util.toReadableDate

// kaart voor één reservatie, met bevestiging in de kaart zelf bij verwijderen
@Composable
fun BookingCard(
    booking: Booking,
    onDeleteConfirmed: () -> Unit,
    onEditClicked: () -> Unit
) {
    var confirmingDelete by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {

            // bovenste rij: foto + merk/model + stad + nummer
            Row(verticalAlignment = Alignment.CenterVertically) {
                CarVisual(
                    brand = booking.brand,
                    imagePath = booking.imagePath,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_card_thumbnail)),
                    showLabel = false
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_small) + 4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${booking.brand} ${booking.model}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = booking.cityName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            R.string.mybookings_reservation_number,
                            booking.bookingId
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

            // periode + totaalprijs in een gekleurd vakje
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_small))
                    )
                    .padding(dimensionResource(R.dimen.status_badge_padding_h))
            ) {
                Column {
                    Text(
                        // formatteer de datum mooi
                        text = stringResource(
                            R.string.mybookings_period,
                            booking.startDate.toReadableDate(),
                            booking.endDate.toReadableDate()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))
                    Text(
                        text = formatEurosFromString(booking.totalPrice),
                        style = MaterialTheme.typography.titleSmall,
                        color = PriceGreen
                    )
                    // toon aantal dagen
                    booking.durationDays?.let { days ->
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))
                        Text(
                            text = stringResource(R.string.booking_duration_days, days),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

            // status bepalen op basis van de datums
            val today = "2026-05-05"
            val statusText: String
            val statusColor: Color
            val isExpired: Boolean
            when {
                booking.endDate < today -> {
                    statusText = stringResource(R.string.mybookings_status_expired)
                    statusColor = Color.Gray
                    isExpired = true
                }
                booking.startDate > today -> {
                    statusText = stringResource(R.string.mybookings_status_future)
                    statusColor = Color(0xFF2196F3)
                    isExpired = false
                }
                else -> {
                    statusText = stringResource(R.string.mybookings_status_active)
                    statusColor = Color(0xFF4CAF50)
                    isExpired = false
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium))
                    )
                    .padding(
                        horizontal = dimensionResource(R.dimen.status_badge_padding_h),
                        vertical = dimensionResource(R.dimen.status_badge_padding_v)
                    )
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

            // bewerken kan enkel als de reservatie niet verlopen is
            val canEdit = !isExpired && booking.carId != null

            Log.d(
                "BookingCard",
                "render #${booking.bookingId} status=${booking.status} " +
                    "carId=${booking.carId} isExpired=$isExpired canEdit=$canEdit"
            )

            if (!confirmingDelete) {
                // 3 knoppen die elk evenveel ruimte krijgen
                val compactPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            Log.d("BookingCard", "Bewerken click — #${booking.bookingId}")
                            onEditClicked()
                        },
                        enabled = canEdit,
                        modifier = Modifier.weight(1f),
                        contentPadding = compactPadding,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.edit_button_label),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                    Button(
                        onClick = {
                            Log.d("BookingCard", "Verwijder click — #${booking.bookingId}")
                            confirmingDelete = true
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = compactPadding,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.mybookings_delete_short),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                    // delen-knop in groen
                    OutlinedButton(
                        onClick = {
                            Log.d("BookingCard", "Delen click — #${booking.bookingId}")
                            shareBooking(context, booking)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = compactPadding,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PriceGreen
                        )
                    ) {
                        Text(
                            text = "📤 " + stringResource(R.string.share_button_label),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            } else {
                // bevestiging tonen
                Text(
                    text = stringResource(R.string.mybookings_delete_confirm_question),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.padding_small)
                    )
                ) {
                    Button(
                        onClick = {
                            Log.d("BookingCard", "Bevestig delete — #${booking.bookingId}")
                            confirmingDelete = false
                            onDeleteConfirmed()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text(text = stringResource(R.string.mybookings_delete_confirm_yes))
                    }
                    Button(
                        onClick = {
                            Log.d("BookingCard", "Annuleer delete — #${booking.bookingId}")
                            confirmingDelete = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.mybookings_delete_confirm_no))
                    }
                }
            }
        }
    }
}

// deel de booking via een share-intent
private fun shareBooking(
    context: android.content.Context,
    booking: Booking
) {
    val shareText = """
        Mijn Rent-a-Car reservering:
        Auto: ${booking.brand} ${booking.model}
        Stad: ${booking.cityName}
        Periode: ${booking.startDate.toReadableDate()} tot en met ${booking.endDate.toReadableDate()}
        Totaalprijs: ${booking.totalPrice} €
    """.trimIndent()

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooserIntent = Intent.createChooser(
        sendIntent,
        context.getString(R.string.share_chooser_title)
    )
    context.startActivity(chooserIntent)
}
