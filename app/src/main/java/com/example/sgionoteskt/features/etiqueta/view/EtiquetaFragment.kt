package com.example.sgionoteskt.features.etiqueta.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.data.model.Etiqueta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EtiquetaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EtiquetaAdapter
    private lateinit var txtVacio: TextView
    private lateinit var etNuevaEtiqueta: EditText
    private lateinit var btnAddTag: ImageView
    private var isAddingTag = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_etiqueta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvEtiquetas)
        txtVacio = view.findViewById(R.id.txtVacio)
        etNuevaEtiqueta = view.findViewById(R.id.txtTagNew)
        btnAddTag = view.findViewById(R.id.btnAddTag)

        adapter = EtiquetaAdapter(
            onFavoriteClick = { etiqueta -> toggleFavorito(etiqueta) },
            onEditComplete = { etiqueta, nuevoNombre -> actualizarNombre(etiqueta, nuevoNombre) },
            onDeleteClick = { etiqueta -> mostrarDialogoEliminar(etiqueta) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddTag.setOnClickListener {
            agregarEtiqueta()
        }

        etNuevaEtiqueta.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                agregarEtiqueta()
                true
            } else {
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            App.database.etiquetaDao().obtenerEtiquetasFlow().collectLatest { etiquetas ->
                adapter.submitList(etiquetas)
                if (etiquetas.isEmpty()) {
                    txtVacio.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    txtVacio.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun agregarEtiqueta() {
        if (isAddingTag) return
        val nombre = etNuevaEtiqueta.text.toString().trim()
        if (nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Ingresa un nombre para la etiqueta", Toast.LENGTH_SHORT).show()
            return
        }

        isAddingTag = true
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.etiquetaDao().insertar(Etiqueta(nombre = nombre))
            withContext(Dispatchers.Main) {
                etNuevaEtiqueta.text.clear()
                Toast.makeText(requireContext(), "Etiqueta creada", Toast.LENGTH_SHORT).show()
                isAddingTag = false
            }
        }
    }

    private fun toggleFavorito(etiqueta: Etiqueta) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val nuevoEstado = if (etiqueta.esFavorito == 1) 0 else 1
            val nuevaFecha = if (nuevoEstado == 1) System.currentTimeMillis() else null
            App.database.etiquetaDao().actualizar(
                etiqueta.copy(esFavorito = nuevoEstado, fechaFavorito = nuevaFecha)
            )
            withContext(Dispatchers.Main) {
                val mensaje = if (nuevoEstado == 1) "Añadido a favoritos" else "Quitado de favoritos"
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarNombre(etiqueta: Etiqueta, nuevoNombre: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.etiquetaDao().actualizar(etiqueta.copy(nombre = nuevoNombre))
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Etiqueta actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoEliminar(etiqueta: Etiqueta) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("¿Está seguro de eliminar la etiqueta \"${etiqueta.nombre}\"?")
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarEtiqueta(etiqueta)
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#4DD0E1"))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.RED)
    }

    private fun eliminarEtiqueta(etiqueta: Etiqueta) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.etiquetaDao().eliminar(etiqueta)
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Etiqueta eliminada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}