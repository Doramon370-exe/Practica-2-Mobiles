package com.example.tarea3backend

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class loginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.203:5000") //modificar para la ip de cada qn
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)

        // Referenciar los elementos
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val etUser = view.findViewById<EditText>(R.id.etUsername)
        val etPass = view.findViewById<EditText>(R.id.etPassword)
        val tvStatus = view.findViewById<TextView>(R.id.miTextView)

        btnLogin.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                tvStatus.text = "Por favor llena todos los campos"
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = api.loginUsuario(UserRequest(user, pass))

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "¡Bienvenido $user!", Toast.LENGTH_SHORT).show()
                        
                        // 1. Desbloqueamos el menú CRUD en la MainActivity
                        (activity as? MainActivity)?.desbloquearMenu()

                        // 2. Navegamos automáticamente al CRUD
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, crudFragment())
                            .commit()

                    } else {
                        tvStatus.text = "Error: Usuario o contraseña incorrectos"
                    }
                } catch (e: Exception) {
                    tvStatus.text = "Error de red: No se pudo conectar"
                }
            }
        }
    }
}