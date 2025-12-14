package com.example.sgionoteskt.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.data.model.NotaConEtiquetas
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Insert
    suspend fun insertar(nota: Nota): Long

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)

    @Transaction
    @Query("SELECT * FROM notas WHERE esta_eliminado = 0 ORDER BY es_favorito DESC, fecha_favorito DESC, ultima_modificacion DESC")
    fun obtenerNotasConEtiquetas(): Flow<List<NotaConEtiquetas>>

    @Query("SELECT * FROM notas WHERE esta_eliminado = 0 ORDER BY es_favorito DESC, fecha_favorito DESC, ultima_modificacion DESC")
    fun obtenerNotas(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE esta_eliminado = 1 ORDER BY ultima_modificacion DESC")
    fun obtenerNotasDePapelera(): Flow<List<Nota>>

    @Transaction
    @Query("SELECT * FROM notas WHERE id_nota = :id")
    suspend fun obtenerNotaConEtiquetas(id: Int): NotaConEtiquetas

    @Query("UPDATE notas SET esta_eliminado = 0, ultima_modificacion = :timestamp WHERE id_nota = :id")
    suspend fun restaurarNota(id: Int, timestamp: Long)

    @Query("DELETE FROM notas WHERE id_nota = :id")
    suspend fun eliminarPermanentemente(id: Int)

    @Query("DELETE FROM notas WHERE esta_eliminado = 1")
    suspend fun vaciarPapelera()

}

