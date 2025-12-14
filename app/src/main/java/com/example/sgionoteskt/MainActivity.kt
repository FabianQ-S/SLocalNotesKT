package com.example.sgionoteskt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.sgionoteskt.app.App
import com.example.sgionoteskt.data.model.UserPreference
import com.example.sgionoteskt.features.etiqueta.view.EtiquetaFragment
import com.example.sgionoteskt.features.nota.view.NotaFragment
import com.example.sgionoteskt.features.nota_detalle.view.NotaDetalleActivity
import com.example.sgionoteskt.features.papelera.view.PapeleraFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var notaFragment: NotaFragment
    private lateinit var etiquetaFragment: EtiquetaFragment
    private lateinit var papeleraFragment: PapeleraFragment

    private lateinit var navigation: BottomNavigationView
    private lateinit var btnNuevaNota: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var avatarContainer: FrameLayout
    private lateinit var avatarIcon: ImageView

    private val avatarMap = mapOf(
        "avatar_1" to R.drawable.ic_avatar_1,
        "avatar_2" to R.drawable.ic_avatar_2,
        "avatar_3" to R.drawable.ic_avatar_3,
        "avatar_4" to R.drawable.ic_avatar_4,
        "avatar_5" to R.drawable.ic_avatar_5,
        "avatar_6" to R.drawable.ic_avatar_6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        btnMenu = findViewById(R.id.btn_menu)
        navigation = findViewById(R.id.bottom_navigation)

        val headerView = navView.getHeaderView(0)
        avatarContainer = headerView.findViewById(R.id.drawer_avatar_container)
        avatarIcon = headerView.findViewById(R.id.drawer_avatar_icon)
        val btnEditProfile = headerView.findViewById<ImageView>(R.id.btn_edit_profile)

        notaFragment = NotaFragment()
        etiquetaFragment = EtiquetaFragment()
        papeleraFragment = PapeleraFragment()

        loadSavedAvatar()
        loadSavedProfile()

        avatarContainer.setOnClickListener {
            showAvatarPickerDialog()
        }

        btnEditProfile.setOnClickListener {
            showProfileEditorDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(
                view.paddingLeft,
                statusBar.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        btnNuevaNota = findViewById(R.id.fab_add_note)

        btnNuevaNota.setOnClickListener {
            val intent = Intent(this, NotaDetalleActivity::class.java)
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        loadFragment(notaFragment)

        if (intent.getBooleanExtra("navigate_to_tags", false)) {
            loadFragment(etiquetaFragment)
            navigation.selectedItemId = R.id.tags
        }

        navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.notes -> {
                    loadFragment(notaFragment)
                    true
                }
                R.id.tags -> {
                    loadFragment(etiquetaFragment)
                    true
                }
                R.id.trash -> {
                    loadFragment(papeleraFragment)
                    true
                }
                else -> false
            }
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_notes -> {
                    loadFragment(notaFragment)
                    navigation.selectedItemId = R.id.notes
                }
                R.id.nav_tags -> {
                    loadFragment(etiquetaFragment)
                    navigation.selectedItemId = R.id.tags
                }
                R.id.nav_trash -> {
                    loadFragment(papeleraFragment)
                    navigation.selectedItemId = R.id.trash
                }
                R.id.nav_github -> {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                    intent.data = android.net.Uri.parse("https://github.com/FabianQ-S/SprivatenotesKT")
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadSavedAvatar() {
        lifecycleScope.launch(Dispatchers.IO) {
            val savedAvatarKey = App.database.userPreferenceDao().getPreference("selected_avatar")
            withContext(Dispatchers.Main) {
                val avatarResId = avatarMap[savedAvatarKey] ?: R.drawable.ic_user
                avatarIcon.setImageResource(avatarResId)
            }
        }
    }

    private fun showAvatarPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_avatar_picker, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val avatarViews = mapOf(
            "avatar_1" to dialogView.findViewById<ImageView>(R.id.avatar1),
            "avatar_2" to dialogView.findViewById<ImageView>(R.id.avatar2),
            "avatar_3" to dialogView.findViewById<ImageView>(R.id.avatar3),
            "avatar_4" to dialogView.findViewById<ImageView>(R.id.avatar4),
            "avatar_5" to dialogView.findViewById<ImageView>(R.id.avatar5),
            "avatar_6" to dialogView.findViewById<ImageView>(R.id.avatar6)
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val currentAvatar = App.database.userPreferenceDao().getPreference("selected_avatar") ?: "avatar_1"
            withContext(Dispatchers.Main) {
                avatarViews.forEach { (key, imageView) ->
                    if (key == currentAvatar) {
                        imageView.setBackgroundResource(R.drawable.avatar_item_selector)
                    } else {
                        imageView.setBackgroundResource(R.drawable.avatar_item_background)
                    }
                }
            }
        }

        val avatarClickListener = View.OnClickListener { view ->
            val avatarKey = when(view.id) {
                R.id.avatar1 -> "avatar_1"
                R.id.avatar2 -> "avatar_2"
                R.id.avatar3 -> "avatar_3"
                R.id.avatar4 -> "avatar_4"
                R.id.avatar5 -> "avatar_5"
                R.id.avatar6 -> "avatar_6"
                else -> "avatar_1"
            }
            saveAvatar(avatarKey)
            dialog.dismiss()
        }

        avatarViews.values.forEach { it.setOnClickListener(avatarClickListener) }

        dialog.show()
    }

    private fun saveAvatar(avatarKey: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            App.database.userPreferenceDao().setPreference(
                UserPreference("selected_avatar", avatarKey)
            )
            withContext(Dispatchers.Main) {
                avatarIcon.setImageResource(avatarMap[avatarKey] ?: R.drawable.ic_user)
            }
        }
    }

    private fun showProfileEditorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_profile_editor, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etUsername = dialogView.findViewById<android.widget.EditText>(R.id.etUsername)
        val etEmail = dialogView.findViewById<android.widget.EditText>(R.id.etEmail)
        val etPhrase = dialogView.findViewById<android.widget.EditText>(R.id.etPhrase)
        val btnCancel = dialogView.findViewById<android.widget.TextView>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<android.widget.TextView>(R.id.btnSave)

        lifecycleScope.launch(Dispatchers.IO) {
            val savedUsername = App.database.userPreferenceDao().getPreference("user_name")
            val savedEmail = App.database.userPreferenceDao().getPreference("user_email")
            val savedPhrase = App.database.userPreferenceDao().getPreference("user_phrase")
            withContext(Dispatchers.Main) {
                etUsername.setText(savedUsername ?: "")
                etEmail.setText(savedEmail ?: "")
                etPhrase.setText(savedPhrase ?: "")
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            var email = etEmail.text.toString().trim()
            val phrase = etPhrase.text.toString().trim()

            if (email.isEmpty()) {
                email = "example@example.com"
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                android.widget.Toast.makeText(this, "Correo electrónico inválido", android.widget.Toast.LENGTH_SHORT).show()
                etEmail.requestFocus()
                return@setOnClickListener
            }

            saveProfile(username, email, phrase)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveProfile(username: String, email: String, phrase: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (username.isNotEmpty()) {
                App.database.userPreferenceDao().setPreference(UserPreference("user_name", username))
            }
            App.database.userPreferenceDao().setPreference(UserPreference("user_email", email))
            App.database.userPreferenceDao().setPreference(UserPreference("user_phrase", phrase))
            withContext(Dispatchers.Main) {
                updateProfileDisplay()
            }
        }
    }

    private fun loadSavedProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val savedUsername = App.database.userPreferenceDao().getPreference("user_name")
            val savedEmail = App.database.userPreferenceDao().getPreference("user_email")
            withContext(Dispatchers.Main) {
                val headerView = navView.getHeaderView(0)
                val userNameView = headerView.findViewById<android.widget.TextView>(R.id.drawer_user_name)
                val userEmailView = headerView.findViewById<android.widget.TextView>(R.id.drawer_user_email)
                userNameView.text = savedUsername ?: "Usuario Ejemplo"
                userEmailView.text = savedEmail ?: "usuario@ejemplo.com"
            }
        }
    }

    private fun updateProfileDisplay() {
        loadSavedProfile()
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedor, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}