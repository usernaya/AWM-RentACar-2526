package com.hssinouimohamedamine.rentacar.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Home : Screen("home")
    object Booking : Screen("booking/{carId}") {
        fun createRoute(carId: Int): String = "booking/$carId"
    }
    object MyBookings : Screen("my_bookings")
}
