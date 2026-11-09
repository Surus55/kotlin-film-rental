package com.example.filmkolcsonzo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "filmkolcsonzo.db"
        private const val DATABASE_VERSION = 1

        // User tábla
        const val TABLE_USER = "User"
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_IS_ADMIN = "isAdmin"

        // Film tábla
        const val TABLE_FILM = "Film"
        const val COL_FILM_ID = "id"
        const val COL_FILM_TITLE_HU = "titleHu"
        const val COL_FILM_TITLE_EN = "titleEn"
        const val COL_FILM_GENRE = "genre"
        const val COL_FILM_YEAR = "year"
        const val COL_FILM_IS_AVAILABLE = "isAvailable"

        // Rental tábla
        const val TABLE_RENTAL = "Rental"
        const val COL_RENTAL_ID = "id"
        const val COL_RENTAL_USER_ID = "userId"
        const val COL_RENTAL_FILM_ID = "filmId"
        const val COL_RENTAL_DATE = "rentalDate"
        const val COL_RENTAL_RETURNED = "returned"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // User tábla
        val createUserTable = """
            CREATE TABLE $TABLE_USER(
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_USERNAME TEXT NOT NULL,
                $COL_USER_PASSWORD TEXT NOT NULL,
                $COL_USER_IS_ADMIN INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createUserTable)

        // Film tábla
        val createFilmTable = """
            CREATE TABLE $TABLE_FILM(
                $COL_FILM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FILM_TITLE_HU TEXT NOT NULL,
                $COL_FILM_TITLE_EN TEXT NOT NULL,
                $COL_FILM_GENRE TEXT NOT NULL,
                $COL_FILM_YEAR INTEGER NOT NULL,
                $COL_FILM_IS_AVAILABLE INTEGER DEFAULT 1
            )
        """.trimIndent()
        db.execSQL(createFilmTable)

        // Rental tábla
        val createRentalTable = """
            CREATE TABLE $TABLE_RENTAL(
                $COL_RENTAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_RENTAL_USER_ID INTEGER NOT NULL,
                $COL_RENTAL_FILM_ID INTEGER NOT NULL,
                $COL_RENTAL_DATE INTEGER NOT NULL,
                $COL_RENTAL_RETURNED INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createRentalTable)

        // Alap adatok feltöltése
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RENTAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FILM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Admin felhasználó
        val adminValues = ContentValues().apply {
            put(COL_USER_USERNAME, "admin")
            put(COL_USER_PASSWORD, "admin123")
            put(COL_USER_IS_ADMIN, 1)
        }
        db.insert(TABLE_USER, null, adminValues)

        // 10 db 1980-as film
        val films = listOf(
            arrayOf("Vissza a jövőbe","Back to the Future","Sci-Fi",1985),
            arrayOf("Indiana Jones és az utolsó kereszteslovag","Indiana Jones and the Last Crusade","Adventure",1989),
            arrayOf("E.T.","E.T. the Extra-Terrestrial","Sci-Fi",1982),
            arrayOf("Ghostbusters","Ghostbusters","Comedy",1984),
            arrayOf("Top Gun","Top Gun","Action",1986),
            arrayOf("The Terminator","The Terminator","Action",1984),
            arrayOf("The Breakfast Club","The Breakfast Club","Drama",1985),
            arrayOf("Blade Runner","Blade Runner","Sci-Fi",1982),
            arrayOf("Ferris Bueller's Day Off","Ferris Bueller's Day Off","Comedy",1986),
            arrayOf("Beetlejuice","Beetlejuice","Comedy",1988)
        )

        films.forEach {
            val values = ContentValues().apply {
                put(COL_FILM_TITLE_HU, it[0] as String)
                put(COL_FILM_TITLE_EN, it[1] as String)
                put(COL_FILM_GENRE, it[2] as String)
                put(COL_FILM_YEAR, it[3] as Int)
                put(COL_FILM_IS_AVAILABLE, 1)
            }
            db.insert(TABLE_FILM, null, values)
        }
    }
}
