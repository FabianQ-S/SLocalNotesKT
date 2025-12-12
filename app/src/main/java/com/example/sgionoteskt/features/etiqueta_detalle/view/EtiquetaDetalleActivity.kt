package com.example.sgionoteskt.features.etiqueta_detalle.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import kotlinx.coroutines.launch

class EtiquetaDetalleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EtiquetaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_etiqueta_detalle)
        setupToolbar()

        val idsSeleccionadas = intent.getIntegerArrayListExtra("idsSeleccionadas") ?: arrayListOf()
        recyclerView = findViewById(R.id.recyclerViewEtiquetas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailTag)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            val etiquetas = App.database.etiquetaDao().obtenerEtiquetas()
            adapter = EtiquetaAdapter(etiquetas, idsSeleccionadas)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@EtiquetaDetalleActivity)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // TODO: LOGICA DE ASOCIACION DE ETIQUETAS SELECCIONADAS CON NOTA
                    devolverEtiquetasSeleccionadas()
                }
            })

    }

    private fun devolverEtiquetasSeleccionadas() {
        val intent = Intent().apply {
            putIntegerArrayListExtra(
                "seleccionadas",
                ArrayList(adapter.seleccionadas)
            )
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                devolverEtiquetasSeleccionadas()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}