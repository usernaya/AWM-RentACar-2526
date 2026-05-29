package be.rentacar.ui.theme

import androidx.compose.ui.graphics.Color

// Brand colors Rent-a-Car — oranje primary, donkere tekstkleuren voor
// goed contrast op witte achtergrond (feedback prof: "contrast letters
// en kleuren, iets donkerder").
val Orange = Color(0xFFFF9500)
val OrangeLight = Color(0xFFFFB04D)
val OrangeDark = Color(0xFFB87000)  // donkerder voor secondary i.p.v. CC7700

// Tekst & oppervlak. BlackPrimary is bijna-zwart i.p.v. pure zwart om
// minder hard te ogen, maar nog steeds WCAG AA contrast (>4.5:1) op wit.
val BlackPrimary = Color(0xFF1A1A1A)
val BlackSurface = Color(0xFF1E1E1E)

val White = Color(0xFFFFFFFF)
val GrayLight = Color(0xFFF5F5F5)
// GrayMedium donkerder gemaakt voor leesbaarheid (was 0xFF9E9E9E — te bleek
// voor secondaire tekst). Nu voldoet aan AA op witte achtergrond.
val GrayMedium = Color(0xFF616161)
// GrayDark donkerder voor secondaire labels op surface.
val GrayDark = Color(0xFF2E2E2E)

// PriceGreen voor prijslabels en de Delen-knop. Material Green 700.
// Reden: de oranje primary was te licht op witte kaarten en groen
// is universeel begrepen als "geld/prijs" — beter contrast (AA).
val PriceGreen = Color(0xFF388E3C)
