package com.example.sgionoteskt.features.papelera.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.data.model.NotaConEtiquetas
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PapeleraAdapter(
    private val onClick: (Nota) -> Unit
) : ListAdapter<NotaConEtiquetas, PapeleraAdapter.PapeleraViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PapeleraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota_papelera, parent, false)
        return PapeleraViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PapeleraViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PapeleraViewHolder(
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

            chipGroup.removeAllViews()
            val etiquetas = notaConEtiquetas.etiquetas
            val maxVisible = 3
            val visibleTags = etiquetas.take(maxVisible)
            val remaining = etiquetas.size - maxVisible
            val themeColor = ContextCompat.getColor(itemView.context, R.color.startGradient)

            visibleTags.forEach { etiqueta ->
                chipGroup.addView(createChip(etiqueta.nombre, themeColor))
            }

            if (remaining > 0) {
                chipGroup.addView(createChip("+$remaining", themeColor))
            }
        }

        private fun createChip(text: String, strokeColor: Int): Chip {
            return Chip(itemView.context).apply {
                this.text = text
                isClickable = false
                isCheckable = false
                setEnsureMinTouchTargetSize(false)
                chipMinHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 28f, resources.displayMetrics
                )
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                chipStartPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                )
                chipEndPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                )
                chipStrokeWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics
                )
                chipStrokeColor = ColorStateList.valueOf(strokeColor)
                chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                setTextColor(Color.DKGRAY)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotaConEtiquetas>() {
        override fun areItemsTheSame(oldItem: NotaConEtiquetas, newItem: NotaConEtiquetas) =
            oldItem.nota.idNota == newItem.nota.idNota

        override fun areContentsTheSame(oldItem: NotaConEtiquetas, newItem: NotaConEtiquetas) =
            oldItem == newItem
    }
}
