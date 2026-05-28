package com.hssinouimohamedamine.rentacar.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hssinouimohamedamine.rentacar.navigation.Screen

@Composable
fun RentACarBottomBar(
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    val items = listOf(
        BottomNavTab(Screen.Home, "H", "Home"),
        BottomNavTab(Screen.MyBookings, "B", "Bookings")
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
                        text = tab.icon,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                label = {
                    Text(text = tab.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

private data class BottomNavTab(
    val screen: Screen,
    val icon: String,
    val label: String
)
