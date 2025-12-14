package com.example.sgionoteskt.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "notas"
)
data class Nota(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_nota")
    val idNota: Int = 0,

    val titulo: String,
    val contenido: String,

    @ColumnInfo(name = "es_favorito")
    val esFavorito: Int = 0,

    @ColumnInfo(name = "fecha_favorito")
    val fechaFavorito: Long? = null,

    @ColumnInfo(name = "esta_eliminado")
    val estaEliminado: Int = 0,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long,

    @ColumnInfo(name = "ultima_modificacion")
    val ultimaModificacion: Long
): Parcelable
