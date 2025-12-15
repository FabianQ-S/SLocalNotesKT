<!-- Centrado y con ancho máximo de 300px -->
<p align="center">
  <img src="icon.jpeg" alt="SLocalNotesKT" width="300px" />
</p>

# SmartGioNotesKT 📍🗺️🚀

Una aplicación Android moderna para crear, organizar y recordar notas asociadas a tu viaje y contexto. Diseñada para viajeros: captura ideas y recuerdos rápidamente, guárdalos con etiquetas y mantenlos organizados en tu dispositivo con almacenamiento local y opciones de backup.

## ✨ Funcionalidades Clave

- 📝 **Creación y Edición de Notas**: Título + contenido con persistencia inmediata
- 🏷️ **Etiquetas (Tags)**: Organiza notas con etiquetas personalizadas
- ⭐ **Favoritos**: Marca notas y etiquetas como favoritas para acceso rápido
- 🗑️ **Papelera**: Eliminación lógica con opción de restauración
- 💾 **Almacenamiento Local SQLite**: Acceso sin conexión garantizado
- 🔄 **Sincronización Local**: Actualizaciones instantáneas en la UI
- 👤 **Perfil Local**: Nombre e ícono personalizable almacenados localmente

## 🧱 Arquitectura

La aplicación sigue una arquitectura **MVVM** (Model-View-ViewModel) clara y escalable:

```
📦 app/src/main/
├── java/com/example/sgionoteskt/
│   ├── 📂 models/              # Entidades ROOM
│   │   ├── Note.kt
│   │   ├── Tag.kt
│   │   ├── UserProfile.kt
│   │   └── NoteTagCrossRef.kt  # Relaciones M2M
│   ├── 📂 database/            # Room Database & DAOs
│   │   ├── NoteDatabase.kt
│   │   ├── NoteDao.kt
│   │   ├── TagDao.kt
│   │   └── NoteTagDao.kt
│   ├── 📂 repository/          # Repository Pattern
│   │   └── NoteRepository.kt
│   ├── 📂 viewmodel/           # ViewModels (MVVM)
│   │   ├── NoteViewModel.kt
│   │   ├── TagViewModel.kt
│   │   └── UserProfileViewModel.kt
│   ├── 📂 ui/
│   │   ├── activities/         # Pantallas principales
│   │   ├── fragments/          # Componentes UI
│   │   └── adapters/           # RecyclerView adapters
│   ├── 📂 utils/               # Utilidades
│   │   ├── ExportImportManager.kt
│   │   └── Constants.kt
│   └── 📂 dialogs/             # Diálogos personalizados
└── res/
    ├── layout/
    ├── values/
    └── drawable/
```

## 🛠️ Stack Tecnológico

| Categoría | Tecnología |
|-----------|-----------|
| **Lenguaje** | Kotlin |
| **Android** | Android SDK 30+ · Material Design 3 |
| **Base de Datos** | SQLite · Room ORM |
| **Arquitectura** | MVVM · Repository Pattern |
| **Asincronía** | Coroutines · Flow |
| **Reactive** | LiveData · StateFlow |
| **Build Tool** | Gradle 8.x |

## 📊 Entidades de Base de Datos

### Note (Tabla: notes)
```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null  // Para papelera
)
```

### Tag (Tabla: tags)
```kotlin
@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: String = "#CCCCCC",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

### NoteTagCrossRef (Tabla: note_tag_cross_ref)
```kotlin
@Entity(
    tableName = "note_tag_cross_ref",
    foreignKeys = [
        ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"])
    ]
)
data class NoteTagCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val tagId: Long
)
```

## 🔄 Flujo de Datos MVVM

```
UI (Activities/Fragments)
        ↓
   ViewModel (LiveData/StateFlow)
        ↓
   Repository
        ↓
   Room DAO
        ↓
   SQLite Database
```

## ⚙️ Dependencias Principales

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.x")
implementation("androidx.room:room-ktx:2.6.x")
kapt("androidx.room:room-compiler:2.6.x")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.x")

// LiveData
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.x")

// Material Design
implementation("com.google.android.material:material:1.11.x")

// Serialización
implementation("com.google.code.gson:gson:2.10.x")
```

## 🚀 Inicio Rápido

### Requisitos
- Android Studio Koala o superior
- Android SDK 30 mínimo (API 30)
- Gradle 8.2+
- Kotlin 1.9+

### Instalación
1. Clona el repositorio
   ```bash
   git clone https://github.com/tu-usuario/SmartGioNotesKT.git
   cd SmartGioNotesKT
   ```

2. Abre el proyecto en Android Studio

3. Sincroniza las dependencias de Gradle

4. Ejecuta la aplicación en un emulador o dispositivo físico

## 🎯 Uso Principal

### Crear una Nota
1. Navega a la pantalla principal
2. Toca el botón flotante `+`
3. Ingresa título y contenido
4. Asigna etiquetas (opcional)
5. Guarda automáticamente


## 📱 Requisitos del Sistema

| Requisito | Versión |
|-----------|---------|
| Android Mínimo | API 30 (Android 11) |
| Android Objetivo | API 36 (Android 15) |
| Java Compatibility | 11+ |
| Kotlin | 1.9+ |

## 🔐 Privacidad y Seguridad

- ✅ **Sin registro online**: Todos los datos se almacenan localmente
- ✅ **Control total**: Los datos permanecen en tu dispositivo
- ✅ **Backups privados**: Exporta tus datos cuando lo necesites
- ✅ **Sin recopilación de datos**: No recopilamos telemetría

## 📚 Documentación Técnica

Para documentación más detallada sobre la arquitectura, DAOs, ViewModels y flujos de datos:
- Ver: [ARQUITECTURA.md](./ARQUITECTURA.md) (si existe)
- Revisar comentarios en el código fuente

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 👥 Información del Equipo

### 👨‍🏫 Docente
**Yovani Edy Quinteros Camapaza** 📚

### 📘 Curso
Desarrollo de Aplicaciones Móviles 1

### 🏫 Institución
Instituto de Educación Superior IDAT

### 📍 Sede
Lima Centro - Petit Thouars

### 🗓️ Sección
V.03.2025-II

### 🧑‍💻 Modalidad
Presencial

---

## 🐙 Integrantes del Equipo

| # | Nombre | Rol |
|---|--------|-----|
| 1 | **Fabian Ricardo Quintanilla Sanchez** | 👨‍💼 Líder del Proyecto |
| 2 | **Omar Alejandro Rios Campos** | 💻 Desarrollador |
| 3 | **Cesar Junior Gamarra Rivera** | 💻 Desarrollador |

---

## 📞 Contacto y Soporte

- 📧 Email: [tu-email@example.com]
- 🐙 GitHub: [SmartGioNotesKT](https://github.com/tu-usuario/SmartGioNotesKT)
- 🔗 Issues: [Reportar problema](https://github.com/tu-usuario/SmartGioNotesKT/issues)

---

**Última actualización**: Enero 2025  
**Versión**: 1.0.0


