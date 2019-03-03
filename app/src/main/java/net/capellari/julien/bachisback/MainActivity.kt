package net.capellari.julien.bachisback

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Attributs
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var appBarConfig: AppBarConfiguration? = null

    // Propriétés
    private val navController get() = findNavController(R.id.nav_host)
    private val isAtTopLevel get()  = navController.currentDestination?.run { isTopLevelDestination(id) } ?: false

    // Events
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout !
        setContentView(R.layout.activity_main)

        // Configs
        setupToolbar()
        setupDrawer()
        setupNavigation()
    }

    override fun onStart() {
        super.onStart()

        // Sync drawerToggle
        if (isAtTopLevel) {
            drawerToggle?.syncState()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || (isAtTopLevel && (drawerToggle?.onOptionsItemSelected(item) ?: false))
                || super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        // Pass to the drawerToggle
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        return appBarConfig?.let { navController.navigateUp(it) } ?: false || super.onSupportNavigateUp()
    }

    // Méthodes
    private fun setupToolbar() {
        // Association de la toolbar du layout
        setSupportActionBar(toolbar)

        // Options
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    private fun setupDrawer() {
        // ActionBarToggle (bouton en haut à gauche avec animation)
        drawerToggle = ActionBarDrawerToggle(
            this, drawer_layout,
            R.string.nav_open, R.string.nav_close
        )

        drawer_layout.addDrawerListener(drawerToggle!!)
    }

    private fun setupNavigation() {
        // Toolbar
        appBarConfig = AppBarConfiguration.Builder(
                    // Top level destinations
                    R.id.fragment_partitions
                ).setDrawerLayout(drawer_layout).build()

        setupActionBarWithNavController(navController, appBarConfig!!)
        navigation_view.setupWithNavController(navController)

        // Navigation Events
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Gestion du drawer
            when (destination.id) {
                R.id.fragment_partitions -> navigation_view.setCheckedItem(R.id.menu_partitions)
            }

            // On cache le drawer si on est pas au top level
            if (isTopLevelDestination(destination.id)) {
                drawerToggle?.syncState()

                drawer_layout.closeDrawers()
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    private fun isTopLevelDestination(@IdRes id: Int) = appBarConfig?.topLevelDestinations?.contains(id) ?: true
}
