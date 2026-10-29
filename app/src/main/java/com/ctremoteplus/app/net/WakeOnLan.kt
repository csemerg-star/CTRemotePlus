package com.ctremoteplus.app.net

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object WakeOnLan {
    fun send(macAddress: String) {
        Thread {
            try {
                val macBytes = macAddress.split(Regex("[:\\-]")).map { it.toInt(16).toByte() }.toByteArray()
                val bytes = ByteArray(6 + 16 * macBytes.size)
                for (i in 0 until 6) bytes[i] = 0xFF.toByte()
                var pos = 6
                for (i in 0 until 16) { System.arraycopy(macBytes, 0, bytes, pos, macBytes.size); pos += macBytes.size }
                val address = InetAddress.getByName("255.255.255.255")
                DatagramSocket().use { it.send(DatagramPacket(bytes, bytes.size, address, 9)) }
            } catch (_: Exception) {}
        }.start()
    }
}
