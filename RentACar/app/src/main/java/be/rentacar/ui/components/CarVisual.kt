package be.rentacar.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import be.rentacar.R

// toont de echte foto van de auto, of een gekleurd vlak als er geen foto is
@Composable
fun CarVisual(
    brand: String,
    imagePath: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val context = LocalContext.current

    // "BMW_X5.jpg" -> "bmw_x5"
    val resourceName = imagePath
        .substringBeforeLast('.')
        .lowercase()

    // 0 = drawable bestaat niet
    val drawableId = context.resources.getIdentifier(
        resourceName,
        "drawable",
        context.packageName
    )

    if (drawableId != 0) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = brand,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(
                RoundedCornerShape(dimensionResource(R.dimen.corner_small))
            )
        )
    } else {
        // geen foto - gebruik de gekleurde fallback
        CarBrandImage(
            brand = brand,
            modifier = modifier,
            showLabel = showLabel
        )
    }
}
