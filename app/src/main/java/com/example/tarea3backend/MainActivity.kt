package com.example.tarea3backend

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Ocultar el CRUD por defecto al iniciar
        navView.menu.findItem(R.id.nav_crud)?.isVisible = false

        // Configura el icono de las 3 rayitas (hamburguesa)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Escuchar los clics del menú
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_login -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, loginFragment())
                        .commit()
                }
                R.id.nav_register -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, RegisterFragment())
                        .commit()
                }
                R.id.nav_crud -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, crudFragment())
                        .commit()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment())
                .commit()
            navView.setCheckedItem(R.id.nav_login)
        }
    }

    // Función para mostrar el CRUD una vez logueado
    fun desbloquearMenu() {
        navView.menu.findItem(R.id.nav_crud)?.isVisible = true
    }
}