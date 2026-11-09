package com.example.filmkolcsonzo.ui

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper

class AdminUsersManageFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24,24,24,24)
        dbHelper = DatabaseHelper(requireContext())

        val etUsername = EditText(requireContext())
        etUsername.hint = "Felhasználónév"
        layout.addView(etUsername)

        val etPassword = EditText(requireContext())
        etPassword.hint = "Jelszó"
        layout.addView(etPassword)

        val btnAdd = Button(requireContext())
        btnAdd.text = "Felhasználó hozzáadása"
        layout.addView(btnAdd)

        val btnDelete = Button(requireContext())
        btnDelete.text = "Felhasználó törlése"
        layout.addView(btnDelete)

        btnAdd.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            if(username.isBlank() || password.isBlank()){
                Toast.makeText(requireContext(),"Töltsd ki mindkét mezőt", Toast.LENGTH_SHORT).show()
            } else {
                val db = dbHelper.writableDatabase
                val cursor = db.rawQuery(
                    "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE username=?",
                    arrayOf(username)
                )
                if(cursor.moveToFirst()){
                    Toast.makeText(requireContext(),"Ez a felhasználónév már létezik", Toast.LENGTH_SHORT).show()
                    cursor.close()
                } else {
                    cursor.close()
                    val values = ContentValues().apply {
                        put(DatabaseHelper.COL_USER_USERNAME, username)
                        put(DatabaseHelper.COL_USER_PASSWORD, password)
                        put(DatabaseHelper.COL_USER_IS_ADMIN, 0)
                    }
                    db.insert(DatabaseHelper.TABLE_USER, null, values)
                    Toast.makeText(requireContext(),"Felhasználó hozzáadva", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnDelete.setOnClickListener {
            val username = etUsername.text.toString()
            if(username.isBlank()){
                Toast.makeText(requireContext(),"Add meg a törlendő felhasználót", Toast.LENGTH_SHORT).show()
            } else {
                val db = dbHelper.writableDatabase
                val deleted = db.delete(DatabaseHelper.TABLE_USER, "${DatabaseHelper.COL_USER_USERNAME}=?", arrayOf(username))
                if(deleted > 0){
                    Toast.makeText(requireContext(),"Felhasználó törölve", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(),"Felhasználó nem található", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return layout
    }
}
