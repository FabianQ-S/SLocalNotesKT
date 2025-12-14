package com.example.sgionoteskt.features.papelera.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.data.model.Nota

class PapeleraAdapter(
    private val onClick: (Nota) -> Unit
) : ListAdapter<Nota, PapeleraAdapter.PapeleraViewHolder>(DiffCallback()) {

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
        private var notaActual: Nota? = null

        init {
            itemView.setOnClickListener {
                notaActual?.let { onClick(it) }
            }
        }

        fun bind(nota: Nota) {
            notaActual = nota
            titulo.text = nota.titulo
            contenido.text = nota.contenido
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Nota>() {
        override fun areItemsTheSame(oldItem: Nota, newItem: Nota) =
            oldItem.idNota == newItem.idNota

        override fun areContentsTheSame(oldItem: Nota, newItem: Nota) =
            oldItem == newItem
    }
}
