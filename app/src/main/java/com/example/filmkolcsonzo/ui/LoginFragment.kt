package com.example.filmkolcsonzo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.data.DatabaseHelper
import com.example.filmkolcsonzo.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())

        // SharedPreferences előkészítése
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        var selectedLang = prefs.getString("lang", "hu") ?: "hu"

        // Nyelvválasztó gombok
        binding.btnLangHu.setOnClickListener {
            selectedLang = "hu"
            prefs.edit().putString("lang", selectedLang).apply()
            Toast.makeText(requireContext(), "Nyelv: Magyar", Toast.LENGTH_SHORT).show()
        }

        binding.btnLangEn.setOnClickListener {
            selectedLang = "en"
            prefs.edit().putString("lang", selectedLang).apply()
            Toast.makeText(requireContext(), "Language: English", Toast.LENGTH_SHORT).show()
        }

        // Naplózás: jelenlegi felhasználók a DB-ben
        val db = dbHelper.readableDatabase
        val cursorLog = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_USER}", null)
        while (cursorLog.moveToNext()) {
            val username = cursorLog.getString(cursorLog.getColumnIndexOrThrow(DatabaseHelper.COL_USER_USERNAME))
            val isAdmin = cursorLog.getInt(cursorLog.getColumnIndexOrThrow(DatabaseHelper.COL_USER_IS_ADMIN))
            Log.d("DB_CHECK", "Felhasználó: $username, admin: $isAdmin")
        }
        cursorLog.close()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Töltsd ki mindkét mezőt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cursor = db.rawQuery(
                "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COL_USER_USERNAME}=? AND ${DatabaseHelper.COL_USER_PASSWORD}=?",
                arrayOf(username, password)
            )

            if (cursor.moveToFirst()) {
                val isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_IS_ADMIN))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))
                cursor.close()

                if (isAdmin == 1) {
                    // Admin Activity (mindig magyar)
                    val intent = Intent(requireContext(), AdminActivity::class.java)
                    startActivity(intent)
                } else {
                    // User Activity
                    val intent = Intent(requireContext(), UserActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("username", username)
                    intent.putExtra("lang", selectedLang) // nyelv átadása
                    startActivity(intent)
                }
            } else {
                cursor.close()
                Toast.makeText(requireContext(), "Helytelen felhasználónév vagy jelszó", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(id, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
