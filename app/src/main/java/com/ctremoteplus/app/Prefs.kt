package com.ctremoteplus.app

import android.content.Context

object Prefs {
    private const val NAME = "ctremote_prefs"
    fun prefs(ctx: Context) = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun setMode(ctx: Context, mode: String) = prefs(ctx).edit().putString("mode", mode).apply()
    fun getMode(ctx: Context) = prefs(ctx).getString("mode", "IR") ?: "IR"

    fun setBrand(ctx: Context, brand: String) = prefs(ctx).edit().putString("brand", brand).apply()
    fun getBrand(ctx: Context) = prefs(ctx).getString("brand", "Samsung") ?: "Samsung"

    fun setDeviceIp(ctx: Context, ip: String) = prefs(ctx).edit().putString("device_ip", ip).apply()
    fun getDeviceIp(ctx: Context) = prefs(ctx).getString("device_ip", "") ?: ""

    fun setDeviceMac(ctx: Context, mac: String) = prefs(ctx).edit().putString("device_mac", mac).apply()
    fun getDeviceMac(ctx: Context) = prefs(ctx).getString("device_mac", "") ?: ""

    fun setWifiBrand(ctx: Context, brand: String) = prefs(ctx).edit().putString("wifi_brand", brand).apply()
    fun getWifiBrand(ctx: Context) = prefs(ctx).getString("wifi_brand", "Samsung (Tizen)") ?: "Samsung (Tizen)"

    fun setCustomAddress(ctx: Context, addr: Int) = prefs(ctx).edit().putInt("custom_addr", addr).apply()
    fun getCustomAddress(ctx: Context) = prefs(ctx).getInt("custom_addr", -1)
}
