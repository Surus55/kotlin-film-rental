package com.example.filmkolcsonzo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class AdminRentalsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)
        dbHelper = DatabaseHelper(requireContext())

        val db = dbHelper.readableDatabase

        val query = """
            SELECT f.${DatabaseHelper.COL_FILM_TITLE_HU} AS title_hu,
                   f.${DatabaseHelper.COL_FILM_TITLE_EN} AS title_en,
                   u.${DatabaseHelper.COL_USER_USERNAME} AS username,
                   r.${DatabaseHelper.COL_RENTAL_DATE} AS rental_date
            FROM ${DatabaseHelper.TABLE_RENTAL} r
            INNER JOIN ${DatabaseHelper.TABLE_FILM} f 
                ON r.${DatabaseHelper.COL_RENTAL_FILM_ID} = f.${DatabaseHelper.COL_FILM_ID}
            INNER JOIN ${DatabaseHelper.TABLE_USER} u 
                ON r.${DatabaseHelper.COL_RENTAL_USER_ID} = u.${DatabaseHelper.COL_USER_ID}
            WHERE r.${DatabaseHelper.COL_RENTAL_RETURNED} = 0
            ORDER BY r.${DatabaseHelper.COL_RENTAL_DATE} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.count == 0) {
            val tv = TextView(requireContext())
            tv.text = "Jelenleg nincs folyamatban lévő kölcsönzés"
            layout.addView(tv)
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            while (cursor.moveToNext()) {
                val titleHu = cursor.getString(cursor.getColumnIndexOrThrow("title_hu"))
                val titleEn = cursor.getString(cursor.getColumnIndexOrThrow("title_en"))
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val rentalTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow("rental_date"))
                val rentalDateStr = sdf.format(Date(rentalTimestamp))

                val tv = TextView(requireContext())
                tv.text = "$titleHu / $titleEn - $username - $rentalDateStr"
                layout.addView(tv)
            }
        }

        cursor.close()
        return layout
    }
}
