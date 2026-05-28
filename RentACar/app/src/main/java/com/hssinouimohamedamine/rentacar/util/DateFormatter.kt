package com.hssinouimohamedamine.rentacar.util

import java.util.Calendar
import java.util.TimeZone

// helpers om datums om te zetten — intern YYYY-MM-DD, in de UI "13 mei 2026"

// "2026-06-01" -> "1 juni 2026"
fun String.toReadableDate(): String {
    val parts = this.split("-")
    if (parts.size != 3) return this
    val year = parts[0].toIntOrNull() ?: return this
    val month = parts[1].toIntOrNull() ?: return this
    val day = parts[2].toIntOrNull() ?: return this

    val monthName = when (month) {
        1 -> "januari"
        2 -> "februari"
        3 -> "maart"
        4 -> "april"
        5 -> "mei"
        6 -> "juni"
        7 -> "juli"
        8 -> "augustus"
        9 -> "september"
        10 -> "oktober"
        11 -> "november"
        12 -> "december"
        else -> return this
    }
    return "$day $monthName $year"
}

// millis uit de DatePicker omzetten naar het formaat dat de backend wil
fun millisToYyyyMmDd(millis: Long): String {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.timeInMillis = millis
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1   // Calendar.MONTH begint bij 0
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return "%04d-%02d-%02d".format(y, m, d)
}

// omgekeerd, terug naar millis voor de DatePicker
fun String.toMillisOrNull(): Long? {
    val parts = this.split("-")
    if (parts.size != 3) return null
    val y = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    val d = parts[2].toIntOrNull() ?: return null
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.clear()
    cal.set(y, m - 1, d)   // Calendar.MONTH begint bij 0
    return cal.timeInMillis
}
