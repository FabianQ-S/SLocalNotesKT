package com.example.sgionoteskt.features.nota.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.sgionoteskt.R
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.data.model.NotaConEtiquetas

class NotaAdapter(
    private val onClick: (Nota) -> Unit
) : ListAdapter<NotaConEtiquetas, NotaViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NotaConEtiquetas>() {
        override fun areItemsTheSame(oldItem: NotaConEtiquetas, newItem: NotaConEtiquetas) =
            oldItem.nota.idNota == newItem.nota.idNota

        override fun areContentsTheSame(oldItem: NotaConEtiquetas, newItem: NotaConEtiquetas) =
            oldItem == newItem
    }
}
