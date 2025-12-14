package com.example.sgionoteskt

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sgionoteskt.features.etiqueta.view.EtiquetaFragment
import com.example.sgionoteskt.features.nota.view.NotaFragment
import com.example.sgionoteskt.features.nota_detalle.view.NotaDetalleActivity
import com.example.sgionoteskt.features.papelera.view.PapeleraFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var notaFragment: NotaFragment
    private lateinit var etiquetaFragment: EtiquetaFragment
    private lateinit var papeleraFragment: PapeleraFragment

    private lateinit var navigation: BottomNavigationView
    private lateinit var btnNuevaNota: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton

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

        notaFragment = NotaFragment()
        etiquetaFragment = EtiquetaFragment()
        papeleraFragment = PapeleraFragment()

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