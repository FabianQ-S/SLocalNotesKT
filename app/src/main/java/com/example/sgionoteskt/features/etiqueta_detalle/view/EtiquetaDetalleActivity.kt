package com.example.sgionoteskt.features.etiqueta_detalle.view

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_etiqueta_detalle)
        setupToolbar()

        recyclerView = findViewById(R.id.recyclerViewEtiquetas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailTag)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            val etiquetas = App.database.etiquetaDao().obtenerEtiquetas()
            val adapter = EtiquetaAdapter(etiquetas)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this@EtiquetaDetalleActivity)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // TODO: LOGICA DE ASOCIACION DE ETIQUETAS SELECCIONADAS CON NOTA
                    finish()
                }
            })

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
                // TODO: LOGICA DE ASOCIACION DE ETIQUETAS SELECCIONADAS CON NOTA
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}