package com.hssinouimohamedamine.rentacar.ui.mybookings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.model.Booking
import com.hssinouimohamedamine.rentacar.ui.components.CarVisual
import com.hssinouimohamedamine.rentacar.ui.theme.PriceGreen
import com.hssinouimohamedamine.rentacar.util.formatEurosFromString
import com.hssinouimohamedamine.rentacar.util.toReadableDate

@Composable
fun BookingCard(
    booking: Booking
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CarVisual(
                    brand = booking.brand,
                    imagePath = booking.imagePath,
                    modifier = Modifier.size(dimensionResource(R.dimen.image_card_thumbnail)),
                    showLabel = false
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.padding_small)))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${booking.brand} ${booking.model}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = booking.cityName,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

            Text(
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
        }
    }
}
