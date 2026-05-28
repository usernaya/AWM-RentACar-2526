package com.hssinouimohamedamine.rentacar.navigation

// alle nav-routes van de app
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Home : Screen("home")
    object Locations : Screen("locations")
    // met bookingId = bewerken, zonder = nieuwe reservering
    object Booking : Screen("booking/{carId}?bookingId={bookingId}") {
        fun createRoute(carId: Int, bookingId: Int? = null): String =
            if (bookingId != null) "booking/$carId?bookingId=$bookingId"
            else "booking/$carId"
    }
    object MyBookings : Screen("my_bookings")
    object Profile : Screen("profile")
}
