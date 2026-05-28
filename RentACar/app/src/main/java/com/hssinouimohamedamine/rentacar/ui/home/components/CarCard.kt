package com.hssinouimohamedamine.rentacar.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.model.Car
import com.hssinouimohamedamine.rentacar.ui.components.CarVisual
import com.hssinouimohamedamine.rentacar.ui.theme.PriceGreen
import com.hssinouimohamedamine.rentacar.util.formatEurosFromString

// kaart voor één auto in de grid
@Composable
fun CarCard(
    car: Car,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            CarVisual(
                brand = car.brand,
                imagePath = car.imagePath,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${car.brand} ${car.model}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = car.cityName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))

            // badge + prijs onder elkaar - grid is smal
            TypeBadge(
                text = car.type,
                backgroundColor = typeColor(car.type)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(
                    R.string.booking_price_per_day,
                    formatEurosFromString(car.pricePerDay)
                ),
                style = MaterialTheme.typography.titleSmall,
                color = PriceGreen,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TypeBadge(text: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_medium))
            )
            .padding(
                horizontal = dimensionResource(R.dimen.status_badge_padding_h),
                vertical = dimensionResource(R.dimen.status_badge_padding_v)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun typeColor(type: String): Color = when (type.lowercase()) {
    "benzine" -> Color(0xFFFF9500).copy(alpha = 0.2f)
    "elektrisch" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
    "hybride" -> Color(0xFF2196F3).copy(alpha = 0.2f)
    else -> Color.Gray.copy(alpha = 0.2f)
}
