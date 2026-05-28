package com.hssinouimohamedamine.rentacar.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hssinouimohamedamine.rentacar.R

// intro scherm voor de eerste keer dat je de app opent
@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.padding_large)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

        Image(
            painter = painterResource(id = R.drawable.welcome_hero),
            contentDescription = stringResource(R.string.welcome_title),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.image_hero_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_large)))
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

        Text(
            text = stringResource(R.string.app_tagline_nl),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))

        FeatureCard(
            icon = "🚗",
            title = stringResource(R.string.welcome_feature_1_title),
            description = stringResource(R.string.welcome_feature_1_desc)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

        FeatureCard(
            icon = "📍",
            title = stringResource(R.string.welcome_feature_2_title),
            description = stringResource(R.string.welcome_feature_2_desc)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

        FeatureCard(
            icon = "📅",
            title = stringResource(R.string.welcome_feature_3_title),
            description = stringResource(R.string.welcome_feature_3_desc)
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.button_height_large))
        ) {
            Text(
                text = stringResource(R.string.welcome_cta),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))

        Text(
            text = stringResource(R.string.welcome_login_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.feature_icon_size))
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_large))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.width(dimensionResource(R.dimen.padding_small) + 4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

