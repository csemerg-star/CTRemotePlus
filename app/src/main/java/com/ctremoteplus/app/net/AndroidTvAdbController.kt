package com.ctremoteplus.app.net

import se.vidstige.jadb.JadbConnection
import java.io.File

class AndroidTvAdbController(private val ip: String, private val keyDir: File) {

    fun sendKeyEvent(keycode: String) {
        Thread {
            try {
                val connection = JadbConnection(ip, 5555)
                val dev = connection.devices.firstOrNull() ?: return@Thread
                dev.executeShell("input", "keyevent", keycode)
            } catch (_: Exception) { }
        }.start()
    }

    fun cycleInput() = sendKeyEvent("178")
}
