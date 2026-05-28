package com.hssinouimohamedamine.rentacar.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hssinouimohamedamine.rentacar.auth.AuthManager
import com.hssinouimohamedamine.rentacar.ui.auth.LoginScreen
import com.hssinouimohamedamine.rentacar.ui.booking.BookingScreen
import com.hssinouimohamedamine.rentacar.ui.components.RentACarBottomBar
import com.hssinouimohamedamine.rentacar.ui.home.HomeScreen
import com.hssinouimohamedamine.rentacar.ui.mybookings.MyBookingsScreen
import com.hssinouimohamedamine.rentacar.ui.welcome.WelcomeScreen

@Composable
fun RentACarNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val startDestination = if (AuthManager.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Welcome.route
    }

    val showBottomBar = currentRoute != null &&
        currentRoute != Screen.Welcome.route &&
        currentRoute != Screen.Login.route &&
        currentRoute != Screen.Booking.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                RentACarBottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Welcome.route) {
                WelcomeScreen(
                    onContinue = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Home.route) {
                HomeScreen(
                    onCarClick = { carId ->
                        navController.navigate(Screen.Booking.createRoute(carId))
                    }
                )
            }

            composable(
                route = Screen.Booking.route,
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: -1
                BookingScreen(
                    carId = carId,
                    onBack = { navController.popBackStack() },
                    onBookingSuccess = {
                        navController.navigate(Screen.MyBookings.route)
                    }
                )
            }

            composable(route = Screen.MyBookings.route) {
                MyBookingsScreen()
            }
        }
    }
}
