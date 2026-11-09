package com.example.filmkolcsonzo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.filmkolcsonzo.R 
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper

class AvailableFilmsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        // Aktuális felhasználó ID lekérése a UserActivity által átadott arguments-ból
        userId = arguments?.getInt("USER_ID") ?: -1
        if (userId == -1) {
            Toast.makeText(requireContext(), "Hiba: Felhasználó nem azonosított!", Toast.LENGTH_SHORT).show()
            return layout
        }

        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_FILM}
            WHERE ${DatabaseHelper.COL_FILM_IS_AVAILABLE} = 1
            ORDER BY ${DatabaseHelper.COL_FILM_TITLE_HU} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0) {
            val tv = TextView(requireContext())
            tv.text = "Jelenleg nincs elérhető film"
            layout.addView(tv)
        } else {
            while (cursor.moveToNext()) {
                val filmId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_ID))
                val titleHu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_HU))
                val titleEn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_EN))

                val tv = TextView(requireContext())
                tv.text = "$titleHu / $titleEn"
                tv.textSize = 18f
                layout.addView(tv)

                val btnRent = Button(requireContext())
                btnRent.text = getString(R.string.btn_rent)
                btnRent.setOnClickListener {
                    // Dinamikus felhasználói ID használata
                    val values = android.content.ContentValues().apply {
                        put(DatabaseHelper.COL_RENTAL_USER_ID, userId)
                        put(DatabaseHelper.COL_RENTAL_FILM_ID, filmId)
                        put(DatabaseHelper.COL_RENTAL_DATE, System.currentTimeMillis())
                        put(DatabaseHelper.COL_RENTAL_RETURNED, 0)
                    }
                    db.insert(DatabaseHelper.TABLE_RENTAL, null, values)

                    val filmValues = android.content.ContentValues().apply {
                        put(DatabaseHelper.COL_FILM_IS_AVAILABLE, 0)
                    }
                    db.update(DatabaseHelper.TABLE_FILM, filmValues, "${DatabaseHelper.COL_FILM_ID}=?", arrayOf(filmId.toString()))

                    Toast.makeText(requireContext(), "A film kölcsönözve!", Toast.LENGTH_SHORT).show()

                    // Frissítés: újra betöltjük a fragmentet
                    val fragment = AvailableFilmsFragment()
                    fragment.arguments = Bundle().apply { putInt("USER_ID", userId) } // ID továbbadása
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container_user, fragment)
                        commit()
                    }
                }
                layout.addView(btnRent)
            }
        }

        cursor.close()
        return layout
    }
}

