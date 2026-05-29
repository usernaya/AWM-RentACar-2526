# Rent-a-Car

Android applicatie voor het huren en beheren van wagens in verschillende Europese steden.

## Projectinformatie

- Vak: Advanced Web and Mobile
- Opleiding: Toegepaste Informatica, Odisee
- Docent: Frank Salliau
- Academiejaar: 2025-2026

## Team

- Aya Boutaarourte
- Mohamed Amine Hssinoui

## Beschrijving

Rent-a-Car is een mobiele applicatie waarmee gebruikers zich kunnen aanmelden, beschikbare wagens kunnen bekijken, filteren en reserveren. Gebruikers kunnen ook hun reservaties beheren en vestigingen bekijken via Google Maps.

## Functionaliteiten

- Aanmelden met Google via Firebase Authentication
- Overzicht van beschikbare wagens
- Filteren op stad, brandstoftype en transmissie
- Sorteren op prijs
- Detailweergave met wageninformatie
- Nieuwe reservatie maken met datumselectie
- Reservatie bewerken
- Reservatie verwijderen
- Reservaties delen
- Vestigingen bekijken en openen in Google Maps
- Profielscherm met accountinformatie

## Technologieen

- Kotlin
- Android Jetpack Compose
- Material 3
- Navigation Compose
- Retrofit
- kotlinx.serialization
- Firebase Authentication
- Google Sign-In
- Gradle Kotlin DSL

## Projectstructuur

```text
RentACar/
  app/
    src/main/java/com/hssinouimohamedamine/rentacar/
      auth/
      model/
      navigation/
      network/
      ui/
      util/
    src/main/res/
  gradle/
```

## Documentatie

Een volledige technische en functionele uitleg van de applicatie staat in:

```text
DOCUMENTATIE.md
```

## Installatie en opstarten

1. Clone de repository.
2. Open de map `RentACar` in Android Studio.
3. Voeg je eigen Firebase configuratiebestand toe:

   ```text
   RentACar/app/google-services.json
   ```

4. Sync het Gradle-project.
5. Start de app op een emulator of Android toestel.

## Belangrijke opmerking

Het bestand `google-services.json` wordt niet mee opgenomen in Git, omdat dit project- en Firebase-configuratie bevat. Elke ontwikkelaar moet dit bestand zelf toevoegen via Firebase Console.

Ook lokale buildbestanden zoals `build/`, `.gradle/`, `.idea/` en `local.properties` horen niet in de repository.
