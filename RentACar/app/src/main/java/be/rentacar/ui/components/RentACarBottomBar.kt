package be.rentacar.ui.components

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import be.rentacar.R
import be.rentacar.navigation.Screen

// onderbalk met 4 tabs
@Composable
fun RentACarBottomBar(
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    val items = listOf(
        BottomNavTab(Screen.Home, emoji = "🏠", labelRes = R.string.bottom_nav_home),
        BottomNavTab(Screen.Locations, emoji = "📍", labelRes = R.string.bottom_nav_locations),
        BottomNavTab(Screen.MyBookings, emoji = "🎫", labelRes = R.string.bottom_nav_bookings),
        BottomNavTab(Screen.Profile, emoji = "👤", labelRes = R.string.bottom_nav_profile)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.screen.route,
                onClick = { onItemClick(tab.screen) },
                icon = {
                    Text(
                        text = tab.emoji,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                label = {
                    Text(text = stringResource(tab.labelRes))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

private data class BottomNavTab(
    val screen: Screen,
    val emoji: String,
    @StringRes val labelRes: Int
)
