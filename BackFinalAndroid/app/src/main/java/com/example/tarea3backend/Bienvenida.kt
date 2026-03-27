package com.example.tarea3backend

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Bienvenida : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        val nombre = intent.getStringExtra("USER_NAME")
        findViewById<TextView>(R.id.tvWelcome).text = "¡Bienvenido, $nombre!"
    }
}