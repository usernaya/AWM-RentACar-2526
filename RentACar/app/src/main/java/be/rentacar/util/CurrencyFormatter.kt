package be.rentacar.util

import java.text.NumberFormat
import java.util.Locale

// format prijs als € (Belgische notatie)
private val belgianLocale: Locale = Locale.forLanguageTag("nl-BE")
private val euroFormatter: NumberFormat =
    NumberFormat.getCurrencyInstance(belgianLocale)

fun formatEuros(amount: Float?): String {
    val safeAmount = amount ?: 0f
    return euroFormatter.format(safeAmount.toDouble())
}

// versie voor String input (prijzen komen als String uit de DB)
fun formatEurosFromString(amountStr: String?): String {
    val parsed = amountStr?.toFloatOrNull() ?: 0f
    return euroFormatter.format(parsed.toDouble())
}
