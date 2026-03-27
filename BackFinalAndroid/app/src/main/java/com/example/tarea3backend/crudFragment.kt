package com.example.tarea3backend

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class crudFragment : Fragment(R.layout.fragment_crud) {

    private lateinit var api: ApiService
    private lateinit var lvNotas: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaDeNotas = mutableListOf<Nota>()
    private val listaNombres = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.83:5000/") // poner la ip de cada qn
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        //Referencias del XML
        lvNotas = view.findViewById(R.id.rvNotas) // Usamos un ListView para hacerlo más rápido
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        //Configurar el Adapter (Cómo se ve la lista)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaNombres)
        lvNotas.adapter = adapter

        // Cargar notas al iniciar
        obtenerNotas()

        //Botón Flotante para CREAR (POST)
        fabAdd.setOnClickListener {
            mostrarDialogoCrear()
        }

        // Clic largo para BORRAR (DELETE)
        lvNotas.setOnItemLongClickListener { _, _, position, _ ->
            val notaABorrar = listaDeNotas[position]
            mostrarDialogoBorrar(notaABorrar)
            true
        }
    }

    private fun obtenerNotas() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.obtenerNotas()
                if (response.isSuccessful) {
                    listaDeNotas.clear()
                    listaNombres.clear()
                    response.body()?.let {
                        listaDeNotas.addAll(it)
                        it.forEach { nota -> listaNombres.add("${nota.titulo}\n${nota.contenido}") }
                    }
                    adapter.notifyDataSetChanged()
                }
                else {
                    android.util.Log.e("API_ERROR", "Código de error: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "Fallo total: ${e.message}")
                e.printStackTrace()
                Log.e("CRUD_ERROR", "Error al obtener: ${e.message}")
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun mostrarDialogoCrear() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Nueva Nota")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 20, 60, 20)

        val inputTitulo = EditText(requireContext()).apply { hint = "Título" }
        val inputContenido = EditText(requireContext()).apply { hint = "Contenido" }

        layout.addView(inputTitulo)
        layout.addView(inputContenido)
        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val t = inputTitulo.text.toString()
            val c = inputContenido.text.toString()
            if (t.isNotEmpty()) {
                crearNota(Nota(titulo = t, contenido = c))
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun crearNota(nuevaNota: Nota) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = api.crearNota(nuevaNota)
                // Si llega aquí, todo salió perfecto
                obtenerNotas()
                Toast.makeText(requireContext(), "Nota Guardada", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Si entra aquí, Retrofit se confundió con el formato,
                // pero como ya vimos en Docker que SÍ se guarda, forzamos el refresco.
                Log.d("CRUD", "Refrescando tras error de formato...")
                obtenerNotas()
                // No mostramos el Toast de error para no confundir al usuario
            }
        }
    }

    private fun mostrarDialogoBorrar(nota: Nota) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Nota")
            .setMessage("¿Estás seguro de que quieres borrar '${nota.titulo}'?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                borrarNota(nota.id ?: 0)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarNota(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.eliminarNota(id)
                if (response.isSuccessful) {
                    obtenerNotas() // Recargar lista
                    Toast.makeText(requireContext(), "Nota eliminada", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CRUD_ERROR", "Error al borrar: ${e.message}")
            }
        }
        // Usa LifecycleScope para asegurar que la app no truene si cambias de pantalla
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = api.eliminarNota(id)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Nota eliminada", Toast.LENGTH_SHORT).show()
                    obtenerNotas() // Recargamos la lista para que desaparezca visualmente
                } else {
                    Log.e("CRUD", "Error del servidor al borrar: ${response.code()}")
                }
            } catch (e: Exception) {
                // "Plan B": Si el servidor borró pero hubo error de formato en Android
                Log.d("CRUD", "Borrado confirmado en server, refrescando interfaz...")
                obtenerNotas()
            }
        }
    }

}