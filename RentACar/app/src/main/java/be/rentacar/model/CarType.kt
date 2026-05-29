package be.rentacar.model

// de 3 mogelijke brandstoftypes
sealed class CarType(val label: String) {
    object Benzine : CarType("Benzine")
    object Elektrisch : CarType("Elektrisch")
    object Hybride : CarType("Hybride")

    companion object {
        fun fromString(type: String): CarType = when (type.lowercase()) {
            "benzine" -> Benzine
            "elektrisch" -> Elektrisch
            "hybride" -> Hybride
            else -> Benzine
        }
    }
}
