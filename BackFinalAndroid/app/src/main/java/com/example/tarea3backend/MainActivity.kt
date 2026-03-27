package com.example.tarea3backend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

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
                    // Carga el Fragment de Login en el contenedor
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

   /* private fun conectarAlBackend() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.50:5000") // Tu IP local
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //la api sitve para convertir los objetos kotlin a JSON
        val api = retrofit.create(ApiService::class.java)

        lifecycleScope.launch {
            try {
                val response = api.verificarApi()
                val tv = findViewById<TextView>(R.id.miTextView)

                if (response.isSuccessful) {
                    // Si todo sale bien (Ejercicio 1) [cite: 63, 64]
                    tv.text = response.body()?.string() ?: "Respuesta vacía"
                } else {
                    tv.text = "Error del servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                // Manejo de errores
                findViewById<TextView>(R.id.miTextView).text = "Error de red: ${e.message}"
            }
        }
    }

    private fun configurarBotones(api: ApiService) {
        btnRegister.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                tvStatus.text = "Por favor llena todos los campos"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = api.registrarUsuario(UserRequest(user, pass))

                    if (response.isSuccessful) {
                        // Caso exitoso
                        tvStatus.text = response.body()?.message
                    } else {
                        // Caso de usuario duplicado u otro error
                        tvStatus.text = "Error: Usuario ya existe"
                    }
                } catch (e: Exception) {
                    tvStatus.text = "Error de conexión"
                }
            }
        }

    }*/


}