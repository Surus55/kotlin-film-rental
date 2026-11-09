package com.example.filmkolcsonzo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper
import com.example.filmkolcsonzo.databinding.FragmentRegisterBinding
import android.content.ContentValues

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Töltsd ki mindkét mezőt", Toast.LENGTH_SHORT).show()
            } else {
                val db = dbHelper.writableDatabase

                // Ellenőrizzük, hogy létezik-e már
                val cursor = db.rawQuery(
                    "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE username=?",
                    arrayOf(username)
                )
                if (cursor.moveToFirst()) {
                    Toast.makeText(requireContext(), "Ez a felhasználónév már létezik", Toast.LENGTH_SHORT).show()
                    cursor.close()
                } else {
                    cursor.close()
                    val values = ContentValues().apply {
                        put(DatabaseHelper.COL_USER_USERNAME, username)
                        put(DatabaseHelper.COL_USER_PASSWORD, password)
                        put(DatabaseHelper.COL_USER_IS_ADMIN, 0)
                    }
                    db.insert(DatabaseHelper.TABLE_USER, null, values)
                    Toast.makeText(requireContext(), "Regisztráció sikeres", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

