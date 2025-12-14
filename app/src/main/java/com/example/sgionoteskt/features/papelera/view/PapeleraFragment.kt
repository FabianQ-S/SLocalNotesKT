package com.example.sgionoteskt.features.papelera.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.data.model.Nota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PapeleraFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PapeleraAdapter
    private lateinit var btnVaciar: Button
    private lateinit var txtVacio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_papelera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvNotasPapelera)
        btnVaciar = view.findViewById(R.id.btnVaciarPapelera)
        txtVacio = view.findViewById(R.id.txtVacio)

        adapter = PapeleraAdapter { nota ->
            mostrarDialogoAccion(nota)
        }

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        btnVaciar.setOnClickListener {
            mostrarDialogoVaciarPapelera()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            App.database.notaDao().obtenerNotasDePapeleraConEtiquetas().collectLatest { notas ->
                adapter.submitList(notas)
                if (notas.isEmpty()) {
                    txtVacio.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    txtVacio.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun mostrarDialogoAccion(nota: Nota) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Desea restaurar o eliminar permanentemente la nota?")
        builder.setNegativeButton("Eliminar") { dialog, _ ->
            eliminarPermanentemente(nota)
            dialog.dismiss()
        }
        builder.setPositiveButton("Restaurar") { dialog, _ ->
            restaurarNota(nota)
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#4DD0E1"))
    }

    private fun mostrarDialogoVaciarPapelera() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("¿Está seguro de borrar todas las notas en papelera?")
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setPositiveButton("Borrar") { dialog, _ ->
            vaciarPapelera()
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#4DD0E1"))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.RED)
    }

    private fun restaurarNota(nota: Nota) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.notaDao().restaurarNota(nota.idNota, System.currentTimeMillis())
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Nota restaurada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarPermanentemente(nota: Nota) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.notaDao().eliminarPermanentemente(nota.idNota)
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Nota eliminada permanentemente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun vaciarPapelera() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            App.database.notaDao().vaciarPapelera()
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Papelera vaciada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}