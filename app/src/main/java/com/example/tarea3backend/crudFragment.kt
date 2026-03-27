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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            .baseUrl("http://192.168.0.203:5000/") //IP de cada quien
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)

        lvNotas = view.findViewById(R.id.rvNotas) 
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        //Configurar el Adapter (Cómo se ve la lista)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaNombres)
        lvNotas.adapter = adapter

        obtenerNotas()

        fabAdd.setOnClickListener {
            mostrarDialogoCrear()
        }

        // Clic largo para BORRAR (DELETE)
        lvNotas.setOnItemLongClickListener { _, _, position, _ ->
            if (position < listaDeNotas.size) {
                mostrarDialogoBorrar(listaDeNotas[position])
            }
            true
        }
    }

    private fun obtenerNotas() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = api.obtenerNotas()
                val notasServidor = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                
                listaDeNotas.clear()
                listaNombres.clear()

                // NOTAS DE PRUEBA HARDCODEADAS (Para cuando no hay DB real)
                listaDeNotas.add(Nota(id = 998, titulo = "Prueba de eliminación", contenido = "Mantén presionado este elemento para probar la función DELETE (Borrar)."))
                listaDeNotas.add(Nota(id = 999, titulo = "Prueba de actualización", contenido = "Toca este elemento una vez para probar la función UPDATE (Actualizar/PUT)."))

                // Agregamos las que vengan del servidor (si hay alguna)
                listaDeNotas.addAll(notasServidor)
                
                listaDeNotas.forEach { nota -> 
                    listaNombres.add("${nota.titulo}\n${nota.contenido}") 
                }
                
                adapter.notifyDataSetChanged()
                
            } catch (e: Exception) {
                // Si el servidor falla, al menos mostramos las notas de prueba
                Log.e("API_ERROR", "Fallo de conexión, cargando notas de prueba local.")
                listaDeNotas.clear()
                listaNombres.clear()
                listaDeNotas.add(Nota(id = 998, titulo = "Prueba de eliminación", contenido = "Mantén presionado este elemento para probar la función DELETE (Borrar)."))
                listaDeNotas.add(Nota(id = 999, titulo = "Prueba de actualización", contenido = "Toca este elemento una vez para probar la función UPDATE (Actualizar/PUT)."))
                listaDeNotas.forEach { nota -> listaNombres.add("${nota.titulo}\n${nota.contenido}") }
                adapter.notifyDataSetChanged()
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
                api.crearNota(nuevaNota)
                Toast.makeText(requireContext(), "Nota Guardada", Toast.LENGTH_SHORT).show()
                delay(500)
                obtenerNotas()
            } catch (e: Exception) {
                obtenerNotas()
            }
        }
    }

    private fun mostrarDialogoEditar(nota: Nota) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Editar Nota")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60, 20, 60, 20)

        val inputTitulo = EditText(requireContext()).apply {
            hint = "Título"
            setText(nota.titulo)
        }
        val inputContenido = EditText(requireContext()).apply {
            hint = "Contenido"
            setText(nota.contenido)
        }

        layout.addView(inputTitulo)
        layout.addView(inputContenido)
        builder.setView(layout)

        builder.setPositiveButton("Actualizar") { _, _ ->
            val t = inputTitulo.text.toString()
            val c = inputContenido.text.toString()
            if (t.isNotEmpty()) {
                actualizarNota(nota.id ?: 0, Nota(titulo = t, contenido = c))
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun actualizarNota(id: Int, notaActualizada: Nota) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                api.actualizarNota(id, notaActualizada)
                Toast.makeText(requireContext(), "Nota actualizada (Simulado)", Toast.LENGTH_SHORT).show()
                delay(300)
                obtenerNotas()
            } catch (e: Exception) {
                obtenerNotas()
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                api.eliminarNota(id)
                Toast.makeText(requireContext(), "Nota eliminada (Simulado)", Toast.LENGTH_SHORT).show()
                delay(300)
                obtenerNotas()
            } catch (e: Exception) {
                obtenerNotas()
            }
        }
    }
}