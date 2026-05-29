package be.rentacar.util

// splitst YYYY-MM-DD op in jaar/maand/dag, null bij rare input
fun parseDateOrNull(dateStr: String): Triple<Int, Int, Int>? {
    val parts = dateStr.split("-")
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null
    if (year !in 2024..2030) return null
    if (month !in 1..12) return null
    if (day !in 1..31) return null
    return Triple(year, month, day)
}

// aantal dagen tussen 2 datums (ruwe schatting met 30d/maand)
fun daysBetween(startDate: String, endDate: String): Int? {
    val start = parseDateOrNull(startDate) ?: return null
    val end = parseDateOrNull(endDate) ?: return null
    val s = start.first * 360 + start.second * 30 + start.third
    val e = end.first * 360 + end.second * 30 + end.third
    val diff = e - s
    return if (diff > 0) diff else null
}
