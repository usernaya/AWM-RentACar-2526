package be.rentacar.data

import be.rentacar.model.Agency

// De API-koppeling voor locaties is niet op tijd gelukt, dus gebruiken we
// voorlopig lokale data. Als uitbreiding kan hiervoor later een endpoint
// toegevoegd worden zodat de locaties uit de backend komen.
object LocalAgencyData {
    val agencies = listOf(
        Agency(
            agencyId = 1,
            companyName = "Hertz",
            cityName = "Brussel",
            country = "Belgie",
            latitude = "50.8467",
            longitude = "4.3525"
        ),
        Agency(
            agencyId = 2,
            companyName = "Avis",
            cityName = "Antwerpen",
            country = "Belgie",
            latitude = "51.2194",
            longitude = "4.4025"
        ),
        Agency(
            agencyId = 3,
            companyName = "Europcar",
            cityName = "Gent",
            country = "Belgie",
            latitude = "51.0543",
            longitude = "3.7174"
        ),
        Agency(
            agencyId = 4,
            companyName = "Sixt",
            cityName = "Brugge",
            country = "Belgie",
            latitude = "51.2093",
            longitude = "3.2247"
        ),
        Agency(
            agencyId = 5,
            companyName = "Enterprise",
            cityName = "Leuven",
            country = "Belgie",
            latitude = "50.8798",
            longitude = "4.7005"
        ),
        Agency(
            agencyId = 6,
            companyName = "Budget",
            cityName = "Luik",
            country = "Belgie",
            latitude = "50.6326",
            longitude = "5.5797"
        ),
        Agency(
            agencyId = 7,
            companyName = "Cambio",
            cityName = "Mechelen",
            country = "Belgie",
            latitude = "51.0259",
            longitude = "4.4775"
        ),
        Agency(
            agencyId = 8,
            companyName = "Rent&Go",
            cityName = "Charleroi",
            country = "Belgie",
            latitude = "50.4108",
            longitude = "4.4446"
        )
    )
}
