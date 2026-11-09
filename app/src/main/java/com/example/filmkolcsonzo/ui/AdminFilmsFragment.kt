package com.example.filmkolcsonzo.ui

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper

class AdminFilmsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinner: Spinner
    private var filmList = mutableListOf<Pair<String, String>>() // Pair<magyar cím, angol cím>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)
        dbHelper = DatabaseHelper(requireContext())

        // Meglévő filmek listázása
        displayFilmList(layout)

        // Film hozzáadáas mezők
        val etTitleHu = EditText(requireContext())
        etTitleHu.hint = "Film magyar címe"
        layout.addView(etTitleHu)

        val etTitleEn = EditText(requireContext())
        etTitleEn.hint = "Film angol címe"
        layout.addView(etTitleEn)

        val etGenre = EditText(requireContext())
        etGenre.hint = "Műfaj"
        layout.addView(etGenre)

        val etYear = EditText(requireContext())
        etYear.hint = "Gyártás éve"
        layout.addView(etYear)

        val btnAddFilm = Button(requireContext())
        btnAddFilm.text = "Film hozzáadása"
        layout.addView(btnAddFilm)

        btnAddFilm.setOnClickListener {
            val titleHu = etTitleHu.text.toString()
            val titleEn = etTitleEn.text.toString()
            val genre = etGenre.text.toString()
            val yearStr = etYear.text.toString()
            if (titleHu.isBlank() || titleEn.isBlank() || genre.isBlank() || yearStr.isBlank()) {
                Toast.makeText(requireContext(), "Töltsd ki az összes mezőt", Toast.LENGTH_SHORT).show()
            } else {
                val year = yearStr.toIntOrNull()
                if (year == null) {
                    Toast.makeText(requireContext(), "Érvénytelen év", Toast.LENGTH_SHORT).show()
                } else {
                    val db = dbHelper.writableDatabase
                    val values = ContentValues().apply {
                        put(DatabaseHelper.COL_FILM_TITLE_HU, titleHu)
                        put(DatabaseHelper.COL_FILM_TITLE_EN, titleEn)
                        put(DatabaseHelper.COL_FILM_GENRE, genre)
                        put(DatabaseHelper.COL_FILM_YEAR, year)
                        put(DatabaseHelper.COL_FILM_IS_AVAILABLE, 1)
                    }
                    db.insert(DatabaseHelper.TABLE_FILM, null, values)
                    Toast.makeText(requireContext(), "Film hozzáadva", Toast.LENGTH_SHORT).show()
                    refreshSpinner()
                    displayFilmList(layout)
                }
            }
        }

        // spinner a törléshez
        spinner = Spinner(requireContext())
        layout.addView(spinner)

        val btnDeleteFilm = Button(requireContext())
        btnDeleteFilm.text = "Film törlése"
        layout.addView(btnDeleteFilm)

        btnDeleteFilm.setOnClickListener {
            val selectedIndex = spinner.selectedItemPosition
            if (selectedIndex >= 0 && selectedIndex < filmList.size) {
                val film = filmList[selectedIndex]
                val db = dbHelper.writableDatabase
                val deleted = db.delete(
                    DatabaseHelper.TABLE_FILM,
                    "${DatabaseHelper.COL_FILM_TITLE_HU}=? AND ${DatabaseHelper.COL_FILM_TITLE_EN}=?",
                    arrayOf(film.first, film.second)
                )
                if (deleted > 0) {
                    Toast.makeText(requireContext(), "Film törölve", Toast.LENGTH_SHORT).show()
                    refreshSpinner()
                    displayFilmList(layout)
                } else {
                    Toast.makeText(requireContext(), "Film nem található", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // spinner inicializálás
        refreshSpinner()

        return layout
    }

    private fun refreshSpinner() {
        filmList.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_FILM} ORDER BY ${DatabaseHelper.COL_FILM_TITLE_EN} ASC", null)
        while (cursor.moveToNext()) {
            val titleHu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_HU))
            val titleEn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_EN))
            filmList.add(Pair(titleHu, titleEn))
        }
        cursor.close()

        val displayList = filmList.map { it.second }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun displayFilmList(layout: LinearLayout) {
        // Töröljük az előző TextView-okat a layoutban (kivéve a hozzáadás + spinner mezőket)
        val removeIndices = mutableListOf<Int>()
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is TextView && child.text.toString() != "Film hozzáadása" && child.text.toString() != "Film törlése") {
                removeIndices.add(i)
            }
        }
        for (i in removeIndices.reversed()) layout.removeViewAt(i)

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_FILM} ORDER BY ${DatabaseHelper.COL_FILM_TITLE_EN} ASC", null)
        while (cursor.moveToNext()) {
            val titleEn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_EN))
            val genre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_GENRE))
            val year = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_YEAR))
            val isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_IS_AVAILABLE))
            val status = if (isAvailable == 1) "Szabad" else "Kölcsönözve"

            val textView = TextView(requireContext())
            textView.text = "$titleEn - $genre - $year - $status"
            layout.addView(textView, 0) // Legfelülre kerül
        }
        cursor.close()
    }
}
