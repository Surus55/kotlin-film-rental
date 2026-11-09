package com.example.filmkolcsonzo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.filmkolcsonzo.R
import com.example.filmkolcsonzo.MainActivity
import java.util.Locale

class UserActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val lang = prefs.getString("lang", "hu") ?: "hu"

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val newContext = newBase.createConfigurationContext(config)

        super.attachBaseContext(newContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        //  Felhasználó ID lekérése
        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        //  Gombok működése
        findViewById<Button>(R.id.btnViewAvailableFilms)?.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_user)
            if (currentFragment !is AvailableFilmsFragment) {
                val fragment = AvailableFilmsFragment()
                fragment.arguments = Bundle().apply { putInt("USER_ID", userId) }
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container_user, fragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        findViewById<Button>(R.id.btnViewMyRentals)?.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_user)
            if (currentFragment !is MyRentalsFragment) {
                val fragment = MyRentalsFragment()
                fragment.arguments = Bundle().apply { putInt("USER_ID", userId) }
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container_user, fragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        findViewById<Button>(R.id.btnLogoutUser)?.setOnClickListener {
            //  Kijelentkezéskor törlés
            val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            prefs.edit().remove("lang").apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
