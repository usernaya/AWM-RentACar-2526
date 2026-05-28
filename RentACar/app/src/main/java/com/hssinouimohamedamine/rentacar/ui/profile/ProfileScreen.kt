package com.hssinouimohamedamine.rentacar.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hssinouimohamedamine.rentacar.R
import com.hssinouimohamedamine.rentacar.auth.AuthManager

// toont user-info + uitlog knop
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    val firebaseUser = AuthManager.currentUser
    ProfileScreenContent(
        displayName = firebaseUser?.displayName,
        email = firebaseUser?.email,
        uid = firebaseUser?.uid,
        onSignOut = onSignOut
    )
}

@Composable
private fun ProfileScreenContent(
    displayName: String?,
    email: String?,
    uid: String?,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // scrollbaar zodat de uitlog-knop ook in landscape bereikbaar blijft
            .verticalScroll(rememberScrollState())
    ) {
        // topbalk
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.screen_profile),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        // fillMaxWidth want fillMaxSize crasht in een scrollende Column
        if (uid == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_extra_large)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.error_no_user_logged_in),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_large)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium)))

                Image(
                    painter = painterResource(id = R.drawable.ic_avatar_default),
                    contentDescription = stringResource(R.string.profile_avatar_desc),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_avatar))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_avatar)))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )

                Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

                Text(
                    text = displayName ?: stringResource(R.string.profile_unknown_user),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(dimensionResource(R.dimen.padding_tiny)))

                Text(
                    text = email ?: stringResource(R.string.profile_unknown_email),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                        Text(
                            text = stringResource(R.string.profile_section_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small) + 4.dp))

                        InfoRow(
                            label = stringResource(R.string.profile_label_name),
                            value = displayName ?: stringResource(R.string.profile_label_dash)
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                        InfoRow(
                            label = stringResource(R.string.profile_label_email),
                            value = email ?: stringResource(R.string.profile_label_dash)
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                        InfoRow(
                            label = stringResource(R.string.profile_label_uid),
                            value = uid.take(16) + "..."
                        )
                    }
                }

                Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))

                Button(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.button_height_large)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = stringResource(R.string.action_logout),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

