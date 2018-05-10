package com.github.abdularis.wifisignalmeter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.github.abdularis.wifisignalmeter.signalmeter.SignalMeterFragment
import com.github.abdularis.wifisignalmeter.timegraph.SignalTimeGraphFragment
import com.github.abdularis.wifisignalmeter.wifilist.WifiListFragment
import com.github.abdularis.wifisignalmeter.wifilist.WifiListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.partial_toolbar.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val STATE_CURRENT_FRAGMENT = "CURRENT_FRAGMENT"

        const val TAG_SIGNAL_METER = "SIGNAL_METER"
        const val TAG_WIFI_LIST = "WIFI_LIST"
        const val TAG_TIME_GRAPH = "TIME_GRAPH"
    }

    private var currentFragmentTag = TAG_SIGNAL_METER
    private lateinit var navigationDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close)
        drawerLayout.addDrawerListener(navigationDrawerToggle)
        navigationView.setNavigationItemSelectedListener(this::selectNavItem)

        var menuId = R.id.menu_signal_meter
        savedInstanceState?.let {
            currentFragmentTag = it.getString(STATE_CURRENT_FRAGMENT, TAG_SIGNAL_METER)
            menuId = when(currentFragmentTag) {
                TAG_WIFI_LIST -> R.id.menu_wifi_list
                TAG_TIME_GRAPH -> R.id.menu_time_graph
                else -> R.id.menu_signal_meter
            }
        }
        selectNavItem(navigationView.menu.findItem(menuId))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        navigationDrawerToggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(STATE_CURRENT_FRAGMENT, currentFragmentTag)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawers()
            currentFragmentTag != TAG_SIGNAL_METER -> selectNavItem(navigationView.menu.findItem(R.id.menu_signal_meter))
            else -> super.onBackPressed()
        }
    }

    private fun selectNavItem(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_signal_meter -> {
                item.isChecked = true
                goToSignalMeter()
            }
            R.id.menu_wifi_list -> {
                item.isChecked = true
                goToWifiList()
            }
            R.id.menu_time_graph -> {
                item.isChecked = true
                goToTimeGraph()
            }
        }

        drawerLayout.closeDrawers()
        return true
    }

    private fun goToSignalMeter() {
        supportActionBar?.title = getString(R.string.title_wifi_signal_meter)
        currentFragmentTag = TAG_SIGNAL_METER
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.content_layout, SignalMeterFragment())
                .commit()
    }

    private fun goToWifiList() {
        supportActionBar?.title = getString(R.string.title_wifi_ap_list)
        currentFragmentTag = TAG_WIFI_LIST
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.content_layout, WifiListFragment())
                .commit()
    }

    private fun goToTimeGraph() {
        supportActionBar?.title = getString(R.string.title_time_graph)
        currentFragmentTag = TAG_TIME_GRAPH
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.content_layout, SignalTimeGraphFragment())
                .commit()
    }
}
