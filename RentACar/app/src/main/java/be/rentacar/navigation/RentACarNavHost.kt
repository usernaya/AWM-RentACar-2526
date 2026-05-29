package be.rentacar.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.rentacar.auth.AuthManager
import be.rentacar.ui.auth.LoginScreen
import be.rentacar.ui.booking.BookingScreen
import be.rentacar.ui.components.RentACarBottomBar
import be.rentacar.ui.home.HomeScreen
import be.rentacar.ui.locations.LocationsScreen
import be.rentacar.ui.mybookings.MyBookingsScreen
import be.rentacar.ui.profile.ProfileScreen
import be.rentacar.ui.welcome.WelcomeScreen

// start op Home als ingelogd, anders Welcome
@Composable
fun RentACarNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val startDestination = if (AuthManager.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Welcome.route
    }

    // bottom bar verbergen op welcome en login
    val showBottomBar = currentRoute != null &&
        currentRoute != Screen.Welcome.route &&
        currentRoute != Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                RentACarBottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { screen ->
                        // klik op Home = terug naar Home, andere tabs gewoon openen
                        if (screen == Screen.Home) {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        } else if (currentRoute != screen.route) {
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
                        // Welcome uit de backstack zodat je niet terug kan
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        // login uit de backstack na succesvol inloggen
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

            composable(route = Screen.Locations.route) {
                LocationsScreen()
            }

            composable(
                route = Screen.Booking.route,
                arguments = listOf(
                    navArgument("carId") { type = NavType.IntType },
                    // -1 = geen bookingId (Int kan niet null zijn)
                    navArgument("bookingId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: -1
                val bookingId = backStackEntry.arguments?.getInt("bookingId") ?: -1
                val editBookingId = if (bookingId == -1) null else bookingId
                BookingScreen(
                    carId = carId,
                    bookingIdToEdit = editBookingId,
                    onBack = { navController.popBackStack() },
                    onBookingSuccess = {
                        // booking-scherm uit de backstack halen
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.MyBookings.route) {
                MyBookingsScreen(
                    onEditBooking = { carId, bookingId ->
                        navController.navigate(
                            Screen.Booking.createRoute(carId, bookingId)
                        )
                    }
                )
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onSignOut = {
                        AuthManager.signOut(context)
                        // hele backstack leegmaken bij uitloggen
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
