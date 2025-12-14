package com.example.sgionoteskt.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sgionoteskt.data.model.Etiqueta
import com.example.sgionoteskt.data.model.EtiquetaNotaCrossRef
import com.example.sgionoteskt.data.model.Nota
import com.example.sgionoteskt.data.local.EtiquetaDao
import com.example.sgionoteskt.data.local.EtiquetaNotaDao
import com.example.sgionoteskt.data.local.NotaDao

@Database(
    entities = [
        Nota::class,
        Etiqueta::class,
        EtiquetaNotaCrossRef::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao
    abstract fun etiquetaDao(): EtiquetaDao
    abstract fun etiquetaNotaDao(): EtiquetaNotaDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notas ADD COLUMN fecha_favorito INTEGER DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notas_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}