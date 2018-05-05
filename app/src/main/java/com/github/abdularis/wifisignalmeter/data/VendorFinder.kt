package com.github.abdularis.wifisignalmeter.data

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

const val DB_NAME = "standard_ieee_oui.db"
const val DB_VERSION = 1
const val TABLE_NAME = "mac_list"
const val COL_MAC = "mac"
const val COL_VENDOR = "vendor"

class DbHelper(ctx: Context): SQLiteAssetHelper(ctx, DB_NAME, null, DB_VERSION)

class VendorFinder(private val dbHelper: DbHelper) {

    fun findVendor(mac: String): String? {
        val vendorId = mac.substring(0, 8).toUpperCase()
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COL_VENDOR), "$COL_MAC=?", arrayOf(vendorId), null, null, null)
        var vendor: String? = null
        if (cursor.moveToFirst()) {
            vendor = cursor.getString(0)
        }
        cursor.close()

        return vendor
    }

}