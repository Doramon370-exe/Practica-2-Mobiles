package com.example.tarea3backend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.203:5000") // Tu IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val etUser = view.findViewById<EditText>(R.id.etRegUsername)
        val etPass = view.findViewById<EditText>(R.id.etRegPassword)
        val tvStatus = view.findViewById<TextView>(R.id.miRegTextView)

        btnRegister.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                tvStatus.text = "Llena todos los campos"
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = api.registrarUsuario(UserRequest(user, pass))
                    if (response.isSuccessful) {
                        tvStatus.text = "¡Registro exitoso! Ya puedes iniciar sesión"
                        tvStatus.setTextColor(android.graphics.Color.GREEN)
                    } else {
                        tvStatus.text = "Error: El usuario ya existe"
                        tvStatus.setTextColor(android.graphics.Color.RED)
                    }
                } catch (e: Exception) {
                    tvStatus.text = "Error de red: ${e.message}"
                }
            }
        }
    }
}