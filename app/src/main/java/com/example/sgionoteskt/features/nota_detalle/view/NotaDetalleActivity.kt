package com.example.sgionoteskt.features.nota_detalle.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.features.etiqueta_detalle.view.EtiquetaDetalleActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotaDetalleActivity : AppCompatActivity() {

    private var nota: Nota? = null
    private lateinit var etTitulo: EditText
    private lateinit var etmDetalleNota: EditText
    private lateinit var btnDelete: FloatingActionButton
    private lateinit var btnEtiqueta: FloatingActionButton
    private lateinit var layoutAcciones: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota_detalle)
        initViews()
        setupToolbar()

        nota = intent.getParcelableExtra("nota")
        nota?.let {
            etTitulo.setText(it.titulo)
            etmDetalleNota.setText(it.contenido)
            layoutAcciones.visibility = View.VISIBLE

            btnDelete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Mover a Papelera")
                    .setMessage("¿Estas seguro de querer mover esta nota a la papelera?")
                    .setPositiveButton("Sí") { _, _ ->
                        moverNotaAPapelera()
                        Toast.makeText(this, "Nota movida a la papelera", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            btnEtiqueta.setOnClickListener {
                val intent = Intent(this,
                    EtiquetaDetalleActivity::class.java)
                startActivity(intent)
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    guardarNotaYCerrar()
                }
            })
    }

    private fun initViews() {
        etTitulo = findViewById(R.id.etTitulo)
        etmDetalleNota = findViewById(R.id.etmDetalleNota)
        btnDelete = findViewById(R.id.fabEliminar)
        btnEtiqueta = findViewById(R.id.fabEtiquetas)
        layoutAcciones = findViewById(R.id.fabButtonsLayout)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailNote)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layoutAcciones.visibility = View.GONE
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
                guardarNotaYCerrar()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun guardarNotaYCerrar() {
        val titulo = etTitulo.text.toString().trim()
        val detalle = etmDetalleNota.text.toString().trim()

        if (titulo.isEmpty() && detalle.isEmpty()) {
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = App.database.notaDao()
            if (nota != null) {
                db.actualizar(
                    nota!!.copy(
                        titulo = titulo,
                        contenido = detalle,
                        ultimaModificacion = System.currentTimeMillis()
                    )
                )
            } else {
                db.insertar(
                    Nota(
                        titulo = titulo,
                        contenido = detalle,
                        fechaCreacion = System.currentTimeMillis(),
                        ultimaModificacion = System.currentTimeMillis()
                    )
                )
            }
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    private fun moverNotaAPapelera() {
        val notaActual = nota ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            val db = App.database.notaDao()
            db.actualizar(
                notaActual.copy(
                    estaEliminado = 1,
                    ultimaModificacion = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }
}
