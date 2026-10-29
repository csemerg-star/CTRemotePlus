package com.ctremoteplus.app.ir

import android.os.Handler
import android.os.Looper

class IrScanner(
    private val controller: IrController,
    private val onProgress: (message: String, address: Int, command: Int) -> Unit
) {
    private var index = 0
    private val handler = Handler(Looper.getMainLooper())
    private var running = false
    private val combos = mutableListOf<Pair<Int, Int>>()
    var lastAddress = 0; private set
    var lastCommand = 0; private set

    init {
        for (addr in IrCodeDatabase.GENERIC_NEC_ADDRESSES)
            for (cmd in IrCodeDatabase.GENERIC_POWER_COMMANDS)
                combos.add(addr to cmd)
    }

    fun start() { running = true; index = 0; step() }
    fun stop() { running = false; handler.removeCallbacksAndMessages(null) }
    fun isRunning() = running

    private fun step() {
        if (!running || index >= combos.size) {
            onProgress("Scan termine - relancez si besoin", lastAddress, lastCommand)
            running = false; return
        }
        val (addr, cmd) = combos[index]
        lastAddress = addr; lastCommand = cmd
        onProgress("Test ${index + 1}/${combos.size} - adresse 0x${addr.toString(16).uppercase()}", addr, cmd)
        controller.sendRawNec(addr, cmd)
        index++
        handler.postDelayed({ step() }, 1100)
    }
}
