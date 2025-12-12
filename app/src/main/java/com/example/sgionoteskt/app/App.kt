package com.example.sgionoteskt.app

import android.app.Application
import androidx.room.Room
import com.example.sgionoteskt.data.db.AppDatabase
import com.example.sgionoteskt.data.model.Etiqueta
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notas_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // agregarEtiquetasIniciales()
    }

    private fun agregarEtiquetasIniciales() {
        GlobalScope.launch {
            val dao = database.etiquetaDao()

            val etiquetasExistentes = dao.obtenerEtiquetas()
            if (etiquetasExistentes.isEmpty()) {
                val etiquetas = listOf(
                    Etiqueta(nombre = "Personal"),
                    Etiqueta(nombre = "Trabajo"),
                    Etiqueta(nombre = "Urgente"),
                    Etiqueta(nombre = "Ideas")
                )

                etiquetas.forEach { etiqueta ->
                    dao.insertar(etiqueta)
                }
            }
        }
    }

}
