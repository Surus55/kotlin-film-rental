package com.example.filmkolcsonzo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.TextView
import com.example.filmkolcsonzo.data.DatabaseHelper

class AdminUsersFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        dbHelper = DatabaseHelper(requireContext())

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USER}", null)

        while (cursor.moveToNext()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_USERNAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD))
            val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_IS_ADMIN))
            val role = if (isAdmin == 1) "Adminisztrátori jogosultság" else "Felhasználói jogosultság"

            val textView = TextView(requireContext())
            textView.text = "Felhasználó: $username, Jelszó: $password, Jogosultság: $role"
            layout.addView(textView)
        }
        cursor.close()

        return layout
    }
}
