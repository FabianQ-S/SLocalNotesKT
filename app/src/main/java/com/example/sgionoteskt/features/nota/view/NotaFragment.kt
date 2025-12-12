package com.example.sgionoteskt.features.nota.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.features.nota_detalle.view.NotaDetalleActivity

class NotaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_nota, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvNotas)
        adapter = NotaAdapter { nota ->
            val intent = Intent(requireContext(), NotaDetalleActivity::class.java)
            intent.putExtra("nota", nota)
            startActivity(intent)
        }

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        lifecycleScope.launchWhenStarted {
            App.database.notaDao().obtenerNotasConEtiquetas().collect { notas ->
                adapter.submitList(notas) {
                    recyclerView.scrollToPosition(0)
                }
            }
        }
    }

}
