package com.example.sgionoteskt.features.etiqueta_detalle.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sgionoteskt.R
import com.example.sgionoteskt.data.model.Etiqueta

class EtiquetaAdapter(
    private val etiquetas: List<Etiqueta>,
    seleccionadasIniciales: List<Int> = listOf()
) : RecyclerView.Adapter<EtiquetaAdapter.EtiquetaViewHolder>() {

    val seleccionadas = seleccionadasIniciales.toMutableSet()

    inner class EtiquetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIconoEtiqueta)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreEtiqueta)
        val cbSeleccionar: CheckBox = itemView.findViewById(R.id.cbSeleccionar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtiquetaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_etiqueta, parent, false)
        return EtiquetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EtiquetaViewHolder, position: Int) {
        val etiqueta = etiquetas[position]
        holder.tvNombre.text = etiqueta.nombre
        holder.ivIcono.setImageResource(R.drawable.tag_icon)

        holder.cbSeleccionar.isChecked = seleccionadas.contains(etiqueta.idEtiqueta)

        holder.cbSeleccionar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                seleccionadas.add(etiqueta.idEtiqueta)
            } else {
                seleccionadas.remove(etiqueta.idEtiqueta)
            }
        }

        holder.itemView.setOnClickListener {
            holder.cbSeleccionar.isChecked = !holder.cbSeleccionar.isChecked
        }
    }

    override fun getItemCount(): Int = etiquetas.size
}
