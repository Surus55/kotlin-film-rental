package com.example.filmkolcsonzo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.filmkolcsonzo.ui.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fragment betöltése induláskor
        if (savedInstanceState == null) {
            val loginFragment: Fragment = LoginFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .commit() // Klasszikus commit, nincs extra import
        }
    }
}
