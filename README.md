Filmkölcsönző – Android alkalmazás

Egyszerű, Kotlin nyelven és Android SDK-val készített filmkölcsönző alkalmazás. A projekt egy helyi SQLite-adatbázisban kezeli a felhasználókat, a filmeket és a kölcsönzéseket. Külön felhasználói és adminisztrátori felületet biztosít.

Funkciók:

Felhasználói felület:
- Regisztráció és bejelentkezés.
- Magyar és angol nyelv kiválasztása a bejelentkezési felületen.
- Az elérhető filmek listázása.
- Film kölcsönzése.
- A saját, folyamatban lévő kölcsönzések megtekintése.
- Kölcsönzött film visszahozása.
- Kijelentkezés.

Adminisztrátori felület:
- Regisztrált felhasználók listázása.
- Felhasználók hozzáadása és törlése.
- Filmek listázása, hozzáadása és törlése.
- Folyamatban lévő kölcsönzések áttekintése.
- Kijelentkezés.

Adatkezelés:
Az alkalmazás SQLite-adatbázist használ, amelyet a `DatabaseHelper.kt` hoz létre és kezel. Az adatbázis fő táblái:

Első indításkor az alkalmazás létrehozza az adatbázist, egy kezdeti adminisztrátori felhasználót és néhány minta filmet.

Technológiák:

- Programozási nyelv:Kotlin
- Platform:Android
- Felhasználói felület:Android Views, XML layoutok, ViewBinding és AndroidX Fragmentek
- Adatbázis:SQLite (`SQLiteOpenHelper`)
- Build rendszer:Gradle Kotlin DSL
- Android Gradle Plugin:8.13.1
- Kotlin:2.0.21
- Gradle Wrapper:8.13

Követelmények:

- Android Studio, amely támogatja az Android Gradle Plugin 8.13.1 verzióját.
- JDK 11 vagy az Android Studio által biztosított kompatibilis JDK.
- Android SDK 36.
- Android 7.0 (API 24) vagy újabb rendszert futtató emulátor vagy fizikai eszköz.
