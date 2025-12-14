package com.example.sgionoteskt.features.nota_detalle.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.sgionoteskt.R
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.data.model.Etiqueta
import com.example.sgionoteskt.data.model.EtiquetaNotaCrossRef
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.features.etiqueta_detalle.view.EtiquetaDetalleActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private lateinit var btnFavorito: FloatingActionButton
    private lateinit var layoutAcciones: LinearLayout
    private lateinit var etiquetaLauncher: ActivityResultLauncher<Intent>
    private lateinit var btnInsertProfile: FloatingActionButton
    private lateinit var btnInsertProfileNew: FloatingActionButton
    private lateinit var fabInsertProfileNewContainer: android.widget.FrameLayout
    private var esFavorito: Boolean = false

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
            fabInsertProfileNewContainer.visibility = View.GONE
            esFavorito = it.esFavorito == 1
            actualizarIconoFavorito()

            btnFavorito.setOnClickListener {
                toggleFavorito()
            }

            btnDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("La nota se enviará a papelera")
                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.setPositiveButton("Aceptar") { dialog, _ ->
                    moverNotaAPapelera()
                    dialog.dismiss()
                }
                val alertDialog = builder.create()
                alertDialog.show()
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(android.graphics.Color.parseColor("#4DD0E1"))
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(android.graphics.Color.parseColor("#4DD0E1"))
            }

            etiquetaLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val seleccionadas =
                        result.data?.getIntegerArrayListExtra("seleccionadas") ?: arrayListOf()

                    asociarEtiquetasANota(seleccionadas) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val notaConEtiquetas = App.database.notaDao().obtenerNotaConEtiquetas(nota!!.idNota)
                            withContext(Dispatchers.Main) {
                                mostrarEtiquetas(notaConEtiquetas.etiquetas)
                            }
                        }
                    }
                }
            }


            btnEtiqueta.setOnClickListener {
                val intent = Intent(this, EtiquetaDetalleActivity::class.java)

                lifecycleScope.launch(Dispatchers.IO) {
                    val notaConEtiquetas = App.database.notaDao().obtenerNotaConEtiquetas(nota!!.idNota)
                    val idsEtiquetas = ArrayList(notaConEtiquetas.etiquetas.map { it.idEtiqueta })

                    withContext(Dispatchers.Main) {
                        intent.putIntegerArrayListExtra("idsSeleccionadas", idsEtiquetas)
                        etiquetaLauncher.launch(intent)
                    }
                }
            }

            btnInsertProfile.setOnClickListener { view ->
                showInsertProfileMenu(view)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val notaConEtiquetas = App.database.notaDao().obtenerNotaConEtiquetas(nota!!.idNota)
                withContext(Dispatchers.Main) {
                    mostrarEtiquetas(notaConEtiquetas.etiquetas)
                }
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
        btnFavorito = findViewById(R.id.fabFavorito)
        btnInsertProfile = findViewById(R.id.fabInsertProfile)
        btnInsertProfileNew = findViewById(R.id.fabInsertProfileNewBtn)
        fabInsertProfileNewContainer = findViewById(R.id.fabInsertProfileNew)
        layoutAcciones = findViewById(R.id.fabButtonsLayout)

        btnInsertProfileNew.setOnClickListener { view ->
            showInsertProfileMenu(view)
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailNote)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        layoutAcciones.visibility = View.GONE
        fabInsertProfileNewContainer.visibility = View.VISIBLE
    }

    private fun actualizarIconoFavorito() {
        if (esFavorito) {
            btnFavorito.setImageResource(R.drawable.ic_star_filled)
        } else {
            btnFavorito.setImageResource(R.drawable.ic_star_outline)
        }
    }

    private fun toggleFavorito() {
        esFavorito = !esFavorito
        actualizarIconoFavorito()

        val notaActual = nota ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            val db = App.database.notaDao()
            val nuevaFechaFavorito = if (esFavorito) System.currentTimeMillis() else null
            nota = notaActual.copy(
                esFavorito = if (esFavorito) 1 else 0,
                fechaFavorito = nuevaFechaFavorito,
                ultimaModificacion = System.currentTimeMillis()
            )
            db.actualizar(nota!!)
            withContext(Dispatchers.Main) {
                val mensaje = if (esFavorito) "Agregado a favoritos" else "Eliminado de favoritos"
                Toast.makeText(this@NotaDetalleActivity, mensaje, Toast.LENGTH_SHORT).show()
            }
        }
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

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            etTitulo.requestFocus()
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
                Toast.makeText(this@NotaDetalleActivity, "Nota enviada a papelera", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun asociarEtiquetasANota(idsEtiquetas: List<Int>, callback: (() -> Unit)? = null) {
        val notaActual = nota ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = App.database.etiquetaNotaDao()

            dao.eliminarRelacionesPorNota(notaActual.idNota)

            idsEtiquetas.forEach { idEtiqueta ->
                dao.insertarRelacion(EtiquetaNotaCrossRef(idEtiqueta, notaActual.idNota))
            }

            withContext(Dispatchers.Main) {
                callback?.invoke()
            }
        }
    }

    private fun mostrarEtiquetas(etiquetas: List<Etiqueta>) {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupSelectedTags)
        chipGroup.removeAllViews()

        etiquetas.forEach { etiqueta ->
            val chip = Chip(this).apply {
                text = etiqueta.nombre
                isClickable = true
                isCheckable = false
                isCloseIconVisible = true
                setChipBackgroundColorResource(R.color.chipSelectedText)
                setTextColor(Color.WHITE)
                closeIconTint = android.content.res.ColorStateList.valueOf(Color.WHITE)
                setOnCloseIconClickListener {
                    eliminarEtiquetaDeNota(etiqueta.idEtiqueta)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun eliminarEtiquetaDeNota(idEtiqueta: Int) {
        val notaActual = nota ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            App.database.etiquetaNotaDao().eliminarRelacion(idEtiqueta, notaActual.idNota)
            val notaConEtiquetas = App.database.notaDao().obtenerNotaConEtiquetas(notaActual.idNota)
            withContext(Dispatchers.Main) {
                mostrarEtiquetas(notaConEtiquetas.etiquetas)
            }
        }
    }

    private fun showInsertProfileMenu(anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.insert_profile_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_insert_phrase -> {
                    insertPhrase()
                    true
                }
                R.id.menu_insert_credentials -> {
                    insertCredentials()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun insertPhrase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val phrase = App.database.userPreferenceDao().getPreference("user_phrase")
            withContext(Dispatchers.Main) {
                if (phrase.isNullOrEmpty()) {
                    Toast.makeText(this@NotaDetalleActivity, "No hay frase favorita guardada", Toast.LENGTH_SHORT).show()
                } else {
                    val currentText = etmDetalleNota.text.toString()
                    val cursorPosition = etmDetalleNota.selectionStart
                    val newText = currentText.substring(0, cursorPosition) + phrase + currentText.substring(cursorPosition)
                    etmDetalleNota.setText(newText)
                    etmDetalleNota.setSelection(cursorPosition + phrase.length)
                }
            }
        }
    }

    private fun insertCredentials() {
        lifecycleScope.launch(Dispatchers.IO) {
            val username = App.database.userPreferenceDao().getPreference("user_name") ?: "Usuario Ejemplo"
            val email = App.database.userPreferenceDao().getPreference("user_email") ?: "usuario@ejemplo.com"
            val credentials = "$username\n$email"
            withContext(Dispatchers.Main) {
                val currentText = etmDetalleNota.text.toString()
                val cursorPosition = etmDetalleNota.selectionStart
                val newText = currentText.substring(0, cursorPosition) + credentials + currentText.substring(cursorPosition)
                etmDetalleNota.setText(newText)
                etmDetalleNota.setSelection(cursorPosition + credentials.length)
            }
        }
    }

}
