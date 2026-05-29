package be.rentacar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import be.rentacar.R

// fallback als er geen foto is: gekleurd vlak met auto-icoon
@Composable
fun CarBrandImage(
    brand: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val brandColor = brandToColor(brand)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_small)))
            .background(brandColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsCar,
                contentDescription = brand,
                tint = Color.White,
                modifier = Modifier.size(if (showLabel) 40.dp else 32.dp)
            )
            if (showLabel) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = brand,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// kies een kleur per merk
private fun brandToColor(brand: String): Color = when (brand.lowercase()) {
    "bmw" -> Color(0xFF1C69D4)         // BMW blauw
    "tesla" -> Color(0xFFCC0000)       // Tesla rood
    "audi" -> Color(0xFF1A1A1A)        // Audi zwart
    "volkswagen" -> Color(0xFF001E50)  // VW donkerblauw
    "toyota" -> Color(0xFFEB0A1E)      // Toyota rood
    "peugeot" -> Color(0xFF1D1D1B)     // Peugeot zwart
    "renault" -> Color(0xFFC4A300)     // Renault donkergeel
    "ford" -> Color(0xFF003478)        // Ford blauw
    "hyundai" -> Color(0xFF002C5F)     // Hyundai donkerblauw
    "skoda" -> Color(0xFF4BA82E)       // Skoda groen
    else -> Color(0xFFFF9500)          // fallback : app-primary oranje
}
