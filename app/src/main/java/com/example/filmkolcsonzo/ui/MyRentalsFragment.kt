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
import java.text.SimpleDateFormat
import java.util.*

class MyRentalsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        // Felhasználó ID lekérése arguments-ból
        currentUserId = arguments?.getInt("USER_ID") ?: -1
        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "Hiba: Felhasználó nem azonosított!", Toast.LENGTH_SHORT).show()
            return layout
        }

        val query = """
            SELECT r.${DatabaseHelper.COL_RENTAL_ID}, f.${DatabaseHelper.COL_FILM_TITLE_HU}, f.${DatabaseHelper.COL_FILM_TITLE_EN}, r.${DatabaseHelper.COL_RENTAL_DATE}
            FROM ${DatabaseHelper.TABLE_RENTAL} r
            INNER JOIN ${DatabaseHelper.TABLE_FILM} f ON r.${DatabaseHelper.COL_RENTAL_FILM_ID} = f.${DatabaseHelper.COL_FILM_ID}
            WHERE r.${DatabaseHelper.COL_RENTAL_USER_ID} = ? AND r.${DatabaseHelper.COL_RENTAL_RETURNED} = 0
            ORDER BY r.${DatabaseHelper.COL_RENTAL_DATE} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(currentUserId.toString()))

        if (cursor.count == 0) {
            val tv = TextView(requireContext())
            tv.text = "Jelenleg nincs folyamatban lévő kölcsönzés"
            layout.addView(tv)
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            while (cursor.moveToNext()) {
                val rentalId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RENTAL_ID))
                val titleHu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_HU))
                val titleEn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILM_TITLE_EN))
                val rentalTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_RENTAL_DATE))
                val rentalDateStr = sdf.format(Date(rentalTimestamp))

                val tv = TextView(requireContext())
                tv.text = "$titleHu / $titleEn - Kölcsönözve: $rentalDateStr"
                tv.textSize = 18f
                layout.addView(tv)

                val btnReturn = Button(requireContext())
                btnReturn.text = getString(R.string.btn_return)
                btnReturn.setOnClickListener {
                    // Rental státusz frissítése
                    val rentalValues = android.content.ContentValues().apply {
                        put(DatabaseHelper.COL_RENTAL_RETURNED, 1)
                    }
                    db.update(DatabaseHelper.TABLE_RENTAL, rentalValues, "${DatabaseHelper.COL_RENTAL_ID}=?", arrayOf(rentalId.toString()))

                    // Film státusz frissítése
                    val filmIdQuery = "SELECT ${DatabaseHelper.COL_RENTAL_FILM_ID} FROM ${DatabaseHelper.TABLE_RENTAL} WHERE ${DatabaseHelper.COL_RENTAL_ID}=?"
                    val filmCursor = db.rawQuery(filmIdQuery, arrayOf(rentalId.toString()))
                    if (filmCursor.moveToFirst()) {
                        val filmId = filmCursor.getInt(filmCursor.getColumnIndexOrThrow(DatabaseHelper.COL_RENTAL_FILM_ID))
                        val filmValues = android.content.ContentValues().apply {
                            put(DatabaseHelper.COL_FILM_IS_AVAILABLE, 1)
                        }
                        db.update(DatabaseHelper.TABLE_FILM, filmValues, "${DatabaseHelper.COL_FILM_ID}=?", arrayOf(filmId.toString()))
                    }
                    filmCursor.close()

                    Toast.makeText(requireContext(), "A film visszahozva!", Toast.LENGTH_SHORT).show()

                    // Frissítés: újra betöltjük a fragmentet
                    val fragment = MyRentalsFragment()
                    fragment.arguments = Bundle().apply { putInt("USER_ID", currentUserId) } // ID továbbadása
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container_user, fragment)
                        commit()
                    }
                }
                layout.addView(btnReturn)
            }
        }

        cursor.close()
        return layout
    }
}
