package com.example.sgionoteskt.features.nota.view

import android.animation.ValueAnimator
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.data.model.NotaConEtiquetas
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class NotaViewHolder(
    itemView: View,
    private val onClick: (Nota) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val titulo = itemView.findViewById<TextView>(R.id.txtTitulo)
    private val contenido = itemView.findViewById<TextView>(R.id.txtContenido)
    private val chipGroup = itemView.findViewById<ChipGroup>(R.id.chipGroupEtiquetas)
    private var notaActual: Nota? = null

    init {
        itemView.setOnClickListener {
            notaActual?.let { onClick(it) }
        }
    }

    fun bind(notaConEtiquetas: NotaConEtiquetas) {
        notaActual = notaConEtiquetas.nota
        titulo.text = notaConEtiquetas.nota.titulo
        contenido.text = notaConEtiquetas.nota.contenido

        // Limpiar chips anteriores
        chipGroup.removeAllViews()

        // Agregar chips
        notaConEtiquetas.etiquetas.forEach { etiqueta ->
            val chip = Chip(itemView.context).apply {
                text = etiqueta.nombre
                isClickable = false
                isCheckable = false
                setPadding(8, 4, 8, 4)
            }
            chipGroup.addView(chip)
        }

        itemView.findViewById<HorizontalScrollView>(R.id.scrollChips)?.let { scroll ->
            scroll.post {
                val maxScroll = chipGroup.width - scroll.width
                if (maxScroll <= 0) return@post

                val animator = ValueAnimator.ofInt(0, maxScroll)
                animator.duration = 8000L
                animator.repeatMode = ValueAnimator.REVERSE
                animator.repeatCount = ValueAnimator.INFINITE
                animator.addUpdateListener { valueAnimator ->
                    val scrollX = valueAnimator.animatedValue as Int
                    scroll.scrollTo(scrollX, 0)
                }
                animator.start()
            }
        }
    }

}
