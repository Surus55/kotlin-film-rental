package com.example.filmkolcsonzo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.filmkolcsonzo.R
import com.example.filmkolcsonzo.MainActivity  

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Regisztrált felhasználók listázása
        findViewById<Button>(R.id.btnUsersList)?.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container_admin, AdminUsersFragment())
                addToBackStack(null)
                commit()
            }
        }

        // Felhasználók kezelése (új felhasználó felvitele/törlés)
        findViewById<Button>(R.id.btnManageUsers)?.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container_admin, AdminUsersManageFragment())
                addToBackStack(null)
                commit()
            }
        }

        // Filmek kezelése (listázás, hozzáadás)
        findViewById<Button>(R.id.btnManageFilms)?.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container_admin, AdminFilmsFragment())
                addToBackStack(null)
                commit()
            }
        }

        // Kölcsönzések adatainak lekérdezése
        findViewById<Button>(R.id.btnViewRentals)?.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_admin)
            if (currentFragment !is AdminRentalsFragment) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container_admin, AdminRentalsFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }

        // Admin kijelentkezés
        findViewById<Button>(R.id.btnLogoutAdmin)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // AdminActivity lezárása, hogy ne lehessen visszalépni
        }
    }
}
