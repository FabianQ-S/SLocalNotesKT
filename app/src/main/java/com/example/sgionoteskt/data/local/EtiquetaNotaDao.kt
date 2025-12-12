package com.example.sgionoteskt.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sgionoteskt.data.model.EtiquetaNotaCrossRef

@Dao
interface EtiquetaNotaDao {

    @Insert
    suspend fun insertarRelacion(relacion: EtiquetaNotaCrossRef)

    @Query("DELETE FROM etiquetas_has_notas WHERE fk_id_etiqueta = :idEtiqueta AND fk_id_nota = :idNota")
    suspend fun eliminarRelacion(idEtiqueta: Int, idNota: Int)

    @Query("DELETE FROM etiquetas_has_notas WHERE fk_id_nota = :idNota")
    suspend fun eliminarRelacionesPorNota(idNota: Int)

}
