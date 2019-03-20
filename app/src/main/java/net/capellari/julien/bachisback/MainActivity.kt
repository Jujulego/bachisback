package net.capellari.julien.bachisback

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Companion
    companion object {
        const val TAG = "MainActivity"
    }

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
        setupNavigation()
    }

    override fun onStart() {
        super.onStart()

        // Sync drawerToggle
        if (isAtTopLevel) {
            drawerToggle?.syncState()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        // Pass to the drawerToggle
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || (isAtTopLevel && (drawerToggle?.onOptionsItemSelected(item) ?: false))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return appBarConfig?.let { navController.navigateUp(it) } ?: false || super.onSupportNavigateUp()
    }

    // Méthodes
    private fun setupNavigation() {
        // Configuration
        appBarConfig = AppBarConfiguration.Builder(navigation_view.menu)
                .setDrawerLayout(drawer_layout)
                .build()

        // Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        setupActionBarWithNavController(navController, appBarConfig!!)

        // Drawer
        drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.nav_open, R.string.nav_close)
        drawer_layout.addDrawerListener(drawerToggle!!)

        navigation_view.setupWithNavController(navController)

        // Navigation Events
        navController.addOnDestinationChangedListener { _, destination, _ ->
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
