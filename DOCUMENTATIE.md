# Rent-a-Car - volledige projectdocumentatie

## 1. Algemene beschrijving

Rent-a-Car is een Android applicatie voor het huren van auto's. De gebruiker kan zich aanmelden met Google, beschikbare wagens bekijken, filteren, sorteren, reserveren en bestaande reservaties beheren. De app communiceert met een PHP backend via Retrofit en gebruikt Firebase Authentication voor Google login.

Het project werd gemaakt voor het vak Advanced Web and Mobile binnen de opleiding Toegepaste Informatica aan Odisee.

## 2. Projectinformatie

- Vak: Advanced Web and Mobile
- Docent: Frank Salliau
- School: Odisee
- Opleiding: Toegepaste Informatica
- Teamleden:
  - Aya Boutaarourte
  - Mohamed Amine Hssinoui

## 3. Doel van de applicatie

Het doel van de applicatie is om een eenvoudige mobiele huurervaring aan te bieden:

1. De gebruiker logt in met Google.
2. De gebruiker ziet een overzicht van beschikbare wagens.
3. De gebruiker kan wagens filteren op stad, brandstof en transmissie.
4. De gebruiker kan sorteren op prijs.
5. De gebruiker kiest een wagen en maakt een reservatie.
6. De gebruiker kan zijn reservaties bekijken, delen, bewerken of verwijderen.
7. De gebruiker kan agentschappen bekijken en openen in Google Maps.
8. De gebruiker kan zijn profielgegevens bekijken en uitloggen.

## 4. Gebruikte technologieen

- Kotlin: programmeertaal van de Android app.
- Android Jetpack Compose: declaratieve UI.
- Material 3: UI componenten en styling.
- Navigation Compose: navigatie tussen schermen.
- ViewModel: schermlogica en state management.
- StateFlow: reactieve UI state.
- Retrofit: HTTP client voor de PHP backend.
- kotlinx.serialization: JSON parsing.
- OkHttp: netwerklaag achter Retrofit.
- Firebase Authentication: Google login.
- Google Sign-In: aanmelden met Google account.
- Gradle Kotlin DSL: buildconfiguratie.

## 5. Globale architectuur

De app volgt een eenvoudige MVVM-achtige structuur:

```text
UI Composables
  -> ViewModel
    -> Retrofit API service
      -> PHP backend
        -> database
```

Voor login:

```text
LoginScreen
  -> LoginViewModel
    -> AuthManager
      -> Google Sign-In
      -> Firebase Authentication
```

Voor data:

```text
HomeScreen / BookingScreen / MyBookingsScreen / LocationsScreen
  -> eigen ViewModel
    -> RentACarApi.retroFitService
      -> RentACarApiService endpoints
```

## 6. Belangrijke flows

### 6.1 Login flow

1. De gebruiker opent de app.
2. `RentACarNavHost` controleert of `AuthManager.isLoggedIn` true is.
3. Indien niet ingelogd start de app op `Welcome`.
4. De gebruiker gaat naar `Login`.
5. `LoginScreen` opent Google Sign-In.
6. Google geeft een `idToken` terug.
7. `LoginViewModel` stuurt dit token naar `AuthManager.signInWithGoogle`.
8. Firebase valideert het token.
9. Bij succes navigeert de app naar `Home`.

### 6.2 Home flow

1. `HomeViewModel` haalt wagens op via `getCars()`.
2. De data wordt opgeslagen in `HomeUiState.Success`.
3. `HomeScreen` toont de lijst in een grid.
4. Filters en sortering worden toegepast op de UI-state.
5. Bij klik op een wagen navigeert de app naar het reservatiescherm.

### 6.3 Reservatie maken

1. De gebruiker kiest een wagen.
2. `BookingScreen` ontvangt een `carId`.
3. `BookingViewModel.loadCar` zoekt de wagen in de lijst van backendwagens.
4. De gebruiker kiest start- en einddatum via Material DatePicker.
5. De app berekent het aantal dagen en de totaalprijs.
6. `BookingViewModel.submitBooking` maakt een `BookingRequest`.
7. De request wordt verstuurd naar `create_booking.php`.
8. Bij succes toont de app een bevestiging.

### 6.4 Reservatie bewerken

1. De gebruiker opent `Mijn reservaties`.
2. Op een reservatie wordt op bewerken geklikt.
3. De app navigeert naar `Booking` met `carId` en `bookingId`.
4. `BookingViewModel.loadCarForEdit` haalt de bestaande reservatie op.
5. De datums worden vooraf ingevuld.
6. `updateBooking` stuurt de aangepaste gegevens naar `update_booking.php`.

### 6.5 Reservatie verwijderen

1. De gebruiker opent `Mijn reservaties`.
2. De gebruiker klikt op verwijderen.
3. `BookingCard` vraagt bevestiging.
4. `MyBookingsViewModel.deleteBooking` stuurt een `DeleteBookingRequest`.
5. De backend verwijdert de reservatie.
6. De lijst wordt opnieuw geladen.

### 6.6 Locaties bekijken

1. `LocationsViewModel` haalt agentschappen op via `getAgencies()`.
2. `LocationsScreen` toont alle agentschappen.
3. Bij klik op "Open in Google Maps" wordt een Android intent gestart.

### 6.7 Profiel en uitloggen

1. `ProfileScreen` leest de actieve Firebase gebruiker via `AuthManager.currentUser`.
2. Naam, email en Firebase UID worden getoond.
3. Bij uitloggen roept de app `AuthManager.signOut(context)` op.
4. De navigatie wordt leeggemaakt en de gebruiker gaat terug naar Login.

## 7. Mappenstructuur

```text
RentACar/
  app/
    src/main/
      java/com/hssinouimohamedamine/rentacar/
        auth/
        model/
        navigation/
        network/
        ui/
        util/
      res/
        drawable/
        mipmap-*/
        values/
        xml/
  gradle/
```

## 8. Uitleg per map

### 8.1 `auth`

Bevat alles rond Firebase Authentication en Google Sign-In.

### 8.2 `model`

Bevat alle data classes die de JSON-data van de backend voorstellen.

### 8.3 `navigation`

Bevat alle routes en de centrale navigatiehost.

### 8.4 `network`

Bevat Retrofit configuratie en de interface met backend endpoints.

### 8.5 `ui`

Bevat alle schermen, componenten, thema's en ViewModels.

### 8.6 `util`

Bevat helperfuncties voor datums en prijsformattering.

## 9. Uitleg per Kotlin bestand

### `MainActivity.kt`

Startpunt van de Android app. Deze activity zet de Compose content op, past `RentACarTheme` toe en toont `RentACarNavHost`.

### `auth/AuthManager.kt`

Centrale helper voor authenticatie.

- Beheert `FirebaseAuth`.
- Geeft de huidige gebruiker terug via `currentUser`.
- Controleert of iemand is ingelogd via `isLoggedIn`.
- Maakt de Google Sign-In client.
- Logt in met Firebase via een Google `idToken`.
- Logt uit bij Firebase en Google.

### `model/Agency.kt`

Data class voor een agentschap of vestiging.

Velden:

- `agencyId`
- `cityName`
- `country`
- `latitude`
- `longitude`

Wordt gebruikt in het locatiescherm.

### `model/Booking.kt`

Data class voor een reservatie die van de backend komt.

Bevat onder andere:

- reservatienummer
- startdatum
- einddatum
- totaalprijs
- status
- wagenmerk en model
- afbeelding
- stad
- optioneel `userId`
- optioneel `carId`

Wordt gebruikt in `MyBookingsScreen` en `BookingCard`.

### `model/BookingExtensions.kt`

Bevat extra helperlogica voor bookings. Dit soort bestand wordt gebruikt om modelgerelateerde functies apart te houden van de UI.

### `model/BookingRequest.kt`

Data class voor het maken van een nieuwe reservatie.

Wordt verstuurd naar `create_booking.php`.

Belangrijke velden:

- Google user id
- car id
- startdatum
- einddatum
- totaalprijs
- email
- volledige naam

### `model/BookingResponse.kt`

Data class voor het antwoord van de backend na create, update of delete.

Velden:

- `success`
- `bookingId`
- `error`

### `model/Car.kt`

Data class voor een wagen.

Bevat:

- car id
- agency id
- brand
- model
- type brandstof
- transmissie
- prijs per dag
- image path
- stad
- latitude
- longitude

Wordt gebruikt in Home en Booking.

### `model/CarType.kt`

Sealed class voor mogelijke brandstoftypes. Dit helpt om brandstoflabels gestructureerd te gebruiken.

### `model/DeleteBookingRequest.kt`

Request body voor het verwijderen van een reservatie.

Bevat:

- `bookingId`
- `googleId`

De `googleId` zorgt ervoor dat de backend weet welke gebruiker de actie uitvoert.

### `model/UpdateBookingRequest.kt`

Request body voor het aanpassen van een reservatie.

Bevat:

- booking id
- car id
- startdatum
- einddatum
- totaalprijs

### `navigation/Screen.kt`

Definieert alle schermroutes in de app.

Routes:

- `welcome`
- `login`
- `home`
- `locations`
- `booking/{carId}?bookingId={bookingId}`
- `my_bookings`
- `profile`

Voor booking bestaat er een helper `createRoute(carId, bookingId)` om correct te navigeren.

### `navigation/RentACarNavHost.kt`

Centrale navigatie van de app.

Taken:

- Bepaalt startscherm op basis van loginstatus.
- Toont of verbergt de bottom navigation.
- Koppelt routes aan schermen.
- Stuurt callbacks door tussen schermen.
- Behandelt navigatie na login, reservatie, bewerken en uitloggen.

Belangrijk:

- Als gebruiker is ingelogd start app op Home.
- Anders start app op Welcome.
- Bottom bar is verborgen op Welcome en Login.
- Booking ondersteunt zowel nieuwe reservatie als bewerken.

### `network/RentACarApi.kt`

Retrofit singleton.

Taken:

- Stelt de base URL in van de PHP backend.
- Configureert JSON parsing met `ignoreUnknownKeys`.
- Configureert OkHttp timeouts.
- Maakt een lazy `RentACarApiService`.

### `network/RentACarApiService.kt`

Retrofit interface met alle backend endpoints.

Endpoints:

- `GET get_agencies.php`
- `GET get_cars.php`
- `GET get_my_bookings.php?google_id=...`
- `POST create_booking.php`
- `POST update_booking.php`
- `POST delete_booking.php`

### `ui/auth/LoginViewModel.kt`

ViewModel voor login.

Beheert `LoginUiState`:

- Idle
- Loading
- Success
- Error

Taken:

- Silent sign-in proberen.
- Google sign-in resultaat verwerken.
- Fouten tonen.
- State resetten.

### `ui/auth/LoginScreen.kt`

Compose scherm voor login.

Taken:

- Maakt Google Sign-In client.
- Start Google login met `rememberLauncherForActivityResult`.
- Toont login knop.
- Toont loading en foutmeldingen.
- Roept `onLoginSuccess` op bij succesvolle login.

### `ui/welcome/WelcomeScreen.kt`

Eerste scherm voor niet-ingelogde gebruikers.

Toont:

- titel
- tagline
- hero afbeelding
- korte feature cards
- knop om naar login te gaan

### `ui/home/HomeUiState.kt`

Definieert de state van het Home scherm.

State:

- `Loading`
- `Success`
- `Error`

`Success` bevat:

- alle wagens
- alle steden
- geselecteerde stad
- geselecteerde brandstof
- geselecteerde transmissie
- sortering

Bevat ook `SortOrder`:

- `None`
- `PriceAsc`
- `PriceDesc`

### `ui/home/HomeViewModel.kt`

ViewModel voor het Home scherm.

Taken:

- Wagens ophalen via backend.
- Stedenlijst opbouwen.
- Geselecteerde filters bewaren.
- Sortering bewaren.
- Filters wissen.
- Data refreshen.

### `ui/home/HomeScreen.kt`

Hoofdscherm na login.

Taken:

- Toont begroeting met gebruikersnaam.
- Toont loading/error/success state.
- Toont filters voor stad, brandstof en transmissie.
- Toont sorteeropties.
- Toont wagens in een grid.
- Past filtering en sorting toe.
- Navigeert naar booking bij klik op een wagen.

Belangrijke interne composables:

- `HomeTopBar`
- `SuccessContent`
- `FiltersSection`
- `FuelFilterRow`
- `TransmissionFilterRow`
- `SortDropdownButton`
- `FilterEmptyState`
- `CityChip`

### `ui/home/components/CarCard.kt`

Herbruikbare kaart voor een wagen.

Toont:

- afbeelding
- merk en model
- stad
- brandstoftype
- transmissie
- prijs per dag

Bij klik opent de booking flow.

### `ui/booking/BookingUiState.kt`

Definieert de state van het reservatiescherm.

State:

- `Loading`
- `FormReady`
- `Submitting`
- `Success`
- `Error`

`FormReady` bevat de wagen en optioneel vooraf ingevulde datums bij bewerken.

### `ui/booking/BookingViewModel.kt`

ViewModel voor nieuwe en bestaande reservaties.

Taken:

- Wagen laden voor nieuwe reservatie.
- Wagen en reservatie laden voor edit mode.
- Nieuwe reservatie aanmaken.
- Bestaande reservatie updaten.
- Firebase gebruiker ophalen.
- Requests naar backend sturen.
- Foutmeldingen afhandelen.

### `ui/booking/BookingScreen.kt`

Compose scherm voor reservaties.

Taken:

- Toont geselecteerde wagen.
- Toont datumkeuze met Material DatePicker.
- Valideert datums.
- Voorkomt startdatum in het verleden.
- Berekent aantal dagen.
- Berekent totaalprijs.
- Toont verschillende UI voor nieuwe reservatie en bewerken.
- Toont succes- en foutschermen.

Belangrijke interne composables:

- `BookingScreenContent`
- `BookingForm`
- `DateButton`
- `CenteredLoading`
- `SubmittingContent`
- `SuccessContent`
- `ErrorContent`

### `ui/mybookings/MyBookingsUiState.kt`

Definieert de state van het scherm Mijn reservaties.

State:

- `Loading`
- `Success`
- `Error`

### `ui/mybookings/MyBookingsViewModel.kt`

ViewModel voor het ophalen en verwijderen van reservaties.

Taken:

- Huidige Firebase gebruiker ophalen.
- Reservaties ophalen via `getMyBookings`.
- Reservatie verwijderen via `deleteBooking`.
- Lijst refreshen na delete.
- Foutmeldingen tonen.

### `ui/mybookings/MyBookingsScreen.kt`

Scherm voor alle reservaties van de gebruiker.

Taken:

- Toont lijst van reservaties.
- Toont lege state als er geen reservaties zijn.
- Toont loading en error state.
- Geeft delete actie door aan ViewModel.
- Geeft edit actie door naar navigatie.

### `ui/mybookings/components/BookingCard.kt`

Kaart voor een individuele reservatie.

Toont:

- auto-afbeelding
- merk en model
- periode
- totaalprijs
- reservatienummer
- status
- delete knop
- edit knop
- share knop

Extra:

- Vraagt bevestiging voor delete.
- Maakt een Android share intent om de reservatie te delen.

### `ui/locations/LocationsUiState.kt`

State voor het locatiescherm.

State:

- `Loading`
- `Success`
- `Error`

### `ui/locations/LocationsViewModel.kt`

ViewModel voor agentschappen.

Taken:

- Agentschappen ophalen via backend.
- Loading, success en error state beheren.
- Refresh ondersteunen.

### `ui/locations/LocationsScreen.kt`

Scherm met alle vestigingen.

Taken:

- Toont lijst van agentschappen.
- Toont loading en error state.
- Opent Google Maps via intent.

### `ui/locations/components/AgencyCard.kt`

Kaart voor een agentschap.

Toont:

- stad
- land
- coordinaten
- knop om te openen in Google Maps

### `ui/profile/ProfileScreen.kt`

Profielscherm.

Toont:

- profielfoto of default avatar
- naam
- email
- Firebase UID
- knop om uit te loggen

Gebruikt `AuthManager.currentUser`.

### `ui/components/RentACarBottomBar.kt`

Bottom navigation component.

Tabs:

- Home
- Vestigingen
- Reservaties
- Profiel

Gebruikt `Screen` objecten om navigatie door te geven.

### `ui/components/CarVisual.kt`

Toont een wagenafbeelding op basis van `imagePath`.

Als de resource niet gevonden wordt, toont de app een fallback via `CarBrandImage`.

### `ui/components/CarBrandImage.kt`

Fallback visual voor een wagenmerk.

Gebruikt merknaam om een herkenbare kleur/visual te tonen wanneer er geen echte afbeelding is.

### `ui/theme/Color.kt`

Definieert kleuren van de applicatie.

### `ui/theme/Shape.kt`

Definieert afgeronde vormen en shapes.

### `ui/theme/Theme.kt`

Compose Material theme van de app.

Bevat:

- light color scheme
- dark color scheme
- dynamic color ondersteuning
- toepassing van typography en color scheme

### `ui/theme/Type.kt`

Definieert typografie voor Material 3.

### `util/CurrencyFormatter.kt`

Helperfuncties voor prijsweergave.

- `formatEuros(amount)`
- `formatEurosFromString(amountStr)`

Zorgt voor consistente euro-notatie.

### `util/DateFormatter.kt`

Helperfuncties voor datumweergave en conversie.

Functies:

- `String.toReadableDate()`
- `millisToYyyyMmDd(millis)`
- `String.toMillisOrNull()`

Wordt vooral gebruikt bij de DatePicker en de leesbare datums.

### `util/DateUtils.kt`

Helperfuncties voor datumvalidatie en duur.

Functies:

- `parseDateOrNull(dateStr)`
- `daysBetween(startDate, endDate)`

Wordt gebruikt om te controleren of datums geldig zijn en om de huurperiode te berekenen.

## 10. Uitleg van resource bestanden

### `res/values/strings.xml`

Bevat alle teksten van de applicatie.

Voordelen:

- Teksten staan centraal.
- UI code blijft schoner.
- App is later makkelijker vertaalbaar.

### `res/values/colors.xml`

XML kleuren voor Android resources.

### `res/values/dimens.xml`

Centrale afmetingen zoals paddings, hoogtes en spacing.

Voordeel:

- Consistente layout.
- Minder magic numbers in UI code.

### `res/values/themes.xml`

Android XML theme dat gekoppeld is aan de activity.

### `res/xml/network_security_config.xml`

Laat HTTP verkeer toe naar de backend. Dit is nodig omdat de backend URL met `http://` werkt.

### `res/xml/backup_rules.xml`

Android backup configuratie.

### `res/xml/data_extraction_rules.xml`

Android data extraction configuratie.

### `res/drawable/*`

Bevat wagenafbeeldingen, welcome image en vector drawables.

Voorbeelden:

- `audi_a3.webp`
- `bmw_x5.webp`
- `tesla_3.jpg`
- `welcome_hero.jpg`
- `ic_avatar_default.xml`

### `res/mipmap-*`

Bevat app launcher icons in verschillende resoluties.

## 11. Build en configuratiebestanden

### Root `.gitignore`

Bepaalt welke bestanden niet in Git mogen komen, zoals:

- `google-services.json`
- `local.properties`
- `.gradle/`
- `build/`
- `.idea/`

### `RentACar/.gitignore`

Android Studio gegenereerde ignore file binnen het Android project.

### `RentACar/settings.gradle.kts`

Definieert de projectnaam `RentACar` en includeert de app module.

### `RentACar/build.gradle.kts`

Root Gradle build file voor plugins en algemene configuratie.

### `RentACar/app/build.gradle.kts`

Belangrijkste Android buildconfiguratie.

Bevat:

- namespace
- applicationId
- compileSdk
- minSdk
- targetSdk
- dependencies
- Compose setup
- Firebase Google Services plugin

### `RentACar/gradle/libs.versions.toml`

Centraliseert versies van dependencies en plugins.

### `gradlew` en `gradlew.bat`

Gradle wrapper scripts om het project te builden zonder lokale Gradle installatie.

## 12. Firebase configuratie

De app gebruikt Firebase Authentication met Google Sign-In.

Nodig:

1. Firebase project.
2. Android app in Firebase met hetzelfde `applicationId` als in `app/build.gradle.kts`.
3. Debug SHA-1 van elke developer.
4. `google-services.json` in:

```text
RentACar/app/google-services.json
```

Belangrijk:

`google-services.json` wordt niet gecommit omdat het projectconfiguratie bevat.

SHA-1 ophalen:

```powershell
cd RentACar
.\gradlew signingReport
```

## 13. Backend communicatie

De backend is een PHP API.

Base URL:

```text
http://mohamedaminehssinoui-odiseebe.webhosting.be/api/
```

Endpoints:

```text
get_agencies.php
get_cars.php
get_my_bookings.php
create_booking.php
update_booking.php
delete_booking.php
```

Retrofit converteert JSON automatisch naar Kotlin data classes.

## 14. State management

Elk groot scherm heeft een eigen `UiState` sealed class.

Voordelen:

- Duidelijke states.
- UI weet precies wat ze moet tonen.
- Loading, success en error worden gescheiden.

Voorbeeld:

```text
Loading -> data ophalen
Success -> data tonen
Error -> foutmelding tonen
```

## 15. Navigatieoverzicht

```text
Welcome
  -> Login
    -> Home
      -> Booking
      -> Locations
      -> MyBookings
        -> Booking edit mode
      -> Profile
        -> Login na uitloggen
```

## 16. Wat is mogelijk in de app?

Een gebruiker kan:

- De app openen op Welcome of Home afhankelijk van loginstatus.
- Inloggen met Google.
- Automatisch ingelogd blijven.
- Uitloggen.
- Alle beschikbare wagens bekijken.
- Wagens filteren per stad.
- Wagens filteren per brandstoftype.
- Wagens filteren per transmissie.
- Wagens sorteren op prijs.
- Filters wissen.
- Een wagen selecteren.
- Een startdatum en einddatum kiezen.
- Totaalprijs zien.
- Reservatie bevestigen.
- Eigen reservaties bekijken.
- Reservatie delen via Android share menu.
- Reservatie bewerken.
- Reservatie verwijderen na bevestiging.
- Vestigingen bekijken.
- Vestiging openen in Google Maps.
- Profielgegevens bekijken.

## 17. Belangrijke aandachtspunten

### Package name / Firebase

Het `applicationId` in `app/build.gradle.kts` moet overeenkomen met de Android app in Firebase.

Als het package verandert, moet ook Firebase opnieuw correct ingesteld worden.

### SHA-1

Elke developer die Google login wil testen moet zijn eigen debug SHA-1 toevoegen in Firebase.

### `google-services.json`

Dit bestand moet lokaal bestaan om Firebase te laten werken, maar hoort niet in Git.

### HTTP backend

Omdat de backend via HTTP werkt, gebruikt de app `network_security_config.xml` en `usesCleartextTraffic=true`.

## 18. Testen op een toestel

1. Activeer Developer Options op Android.
2. Activeer USB debugging.
3. Verbind toestel via USB.
4. Open project in Android Studio.
5. Kies het toestel.
6. Klik op Run.

Voor Google login moet de SHA-1 van de computer waarmee je buildt in Firebase staan.

## 19. Mogelijke verbeteringen

- Repository pattern toevoegen tussen ViewModel en API.
- Dependency injection gebruiken, bijvoorbeeld Hilt.
- Betere datumlogica met `java.time`.
- Unit tests uitbreiden voor ViewModels.
- UI tests toevoegen voor reservatieflow.
- HTTPS backend gebruiken in plaats van HTTP.
- Meertaligheid toevoegen.
- Offline caching toevoegen.

## 20. Korte conclusie

Rent-a-Car is een volledige Android applicatie met login, backendcommunicatie, filtering, reservaties, bewerken, verwijderen, delen, locaties en profielbeheer. De app gebruikt moderne Android technologieen zoals Jetpack Compose, ViewModel, StateFlow, Retrofit en Firebase Authentication.
