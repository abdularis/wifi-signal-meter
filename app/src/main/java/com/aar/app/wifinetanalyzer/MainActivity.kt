package com.aar.app.wifinetanalyzer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.aar.app.wifinetanalyzer.about.AboutActivity
import com.aar.app.wifinetanalyzer.ouilookup.OuiLookupFragment
import com.aar.app.wifinetanalyzer.signalmeter.SignalMeterFragment
import com.aar.app.wifinetanalyzer.timegraph.SignalTimeGraphFragment
import com.aar.app.wifinetanalyzer.wifilist.WifiListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.partial_toolbar.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val STATE_CURRENT_FRAGMENT = "CURRENT_FRAGMENT"
    }

    private var currentFragment = R.id.menu_signal_meter
    private lateinit var navigationDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close)
        drawerLayout.addDrawerListener(navigationDrawerToggle)
        navigationView.setNavigationItemSelectedListener(this::selectNavItem)

        savedInstanceState?.let {
            currentFragment = it.getInt(STATE_CURRENT_FRAGMENT, currentFragment)
        }
        goToScreen(currentFragment, animate = false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(STATE_CURRENT_FRAGMENT, currentFragment)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navigationDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawers()
            currentFragment != R.id.menu_signal_meter -> goToSignalMeter()
            else -> super.onBackPressed()
        }
    }

    private fun selectNavItem(item: MenuItem): Boolean {
        if (item.itemId != currentFragment) {
            goToScreen(item.itemId)
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun replaceFragment(fragment: Fragment, menuId: Int, animate: Boolean = true) {
        currentFragment = menuId
        navigationView.menu.findItem(menuId).isChecked = true
        val transaction = supportFragmentManager.beginTransaction()
        if (animate) transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction
                .replace(R.id.content_layout, fragment)
                .commit()
    }

    private fun goToScreen(itemId: Int, animate: Boolean = true) {
        when (itemId) {
            R.id.menu_signal_meter -> goToSignalMeter(animate)
            R.id.menu_wifi_list -> goToWifiList(animate)
            R.id.menu_time_graph -> goToTimeGraph(animate)
            R.id.menu_oui_lookup -> goToOuiLookup(animate)
            R.id.menu_about -> goToAbout()
        }
    }

    private fun goToSignalMeter(animate: Boolean = true) {
        supportActionBar?.title = getString(R.string.title_wifi_signal_meter)
        replaceFragment(SignalMeterFragment(), R.id.menu_signal_meter, animate)
    }

    private fun goToWifiList(animate: Boolean = true) {
        supportActionBar?.title = getString(R.string.title_wifi_ap_list)
        replaceFragment(WifiListFragment(), R.id.menu_wifi_list, animate)
    }

    private fun goToTimeGraph(animate: Boolean = true) {
        supportActionBar?.title = getString(R.string.title_time_graph)
        replaceFragment(SignalTimeGraphFragment(), R.id.menu_time_graph, animate)
    }

    private fun goToOuiLookup(animate: Boolean = true) {
        supportActionBar?.title = getString(R.string.title_oui_lookup)
        replaceFragment(OuiLookupFragment(), R.id.menu_oui_lookup, animate)
    }

    private fun goToAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }
}
