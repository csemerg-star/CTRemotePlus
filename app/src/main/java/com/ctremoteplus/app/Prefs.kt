package com.ctremoteplus.app
import android.content.Context

object Prefs {
    private const val NAME = "ctremote_prefs"
    fun prefs(ctx: Context) = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    fun setMode(ctx: Context, mode: String) = prefs(ctx).edit().putString("mode", mode).apply()
    fun getMode(ctx: Context) = prefs(ctx).getString("mode", "IR") ?: "IR"
    fun setBrand(ctx: Context, brand: String) = prefs(ctx).edit().putString("brand", brand).apply()
    fun getBrand(ctx: Context) = prefs(ctx).getString("brand", "Samsung") ?: "Samsung"
    fun setDeviceType(ctx: Context, type: String) = prefs(ctx).edit().putString("device_type", type).apply()
    fun getDeviceType(ctx: Context) = prefs(ctx).getString("device_type", "TV") ?: "TV"
    fun setCustomAddress(ctx: Context, addr: Int) = prefs(ctx).edit().putInt("custom_addr", addr).apply()
    fun getCustomAddress(ctx: Context) = prefs(ctx).getInt("custom_addr", -1)
}
