package com.ctremoteplus.app.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.widget.Toast

class IrController(private val context: Context) {
    private val manager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    fun sendCommand(set: IrCodeSet, key: String) {
        val code = set.codes[key] ?: run {
            Toast.makeText(context, "Code non disponible pour $key", Toast.LENGTH_SHORT).show()
            return
        }
        val pattern = when (set.protocol) {
            "NEC32" -> IrProtocols.nec32(code)
            "SIRC12" -> IrProtocols.sirc(code.toInt(), 1)
            "NEC_ADDR" -> IrProtocols.necAddrCmd(set.address ?: 0, code.toInt())
            else -> IrProtocols.nec32(code)
        }
        transmit(IrProtocols.NEC_FREQ, pattern)
    }

    fun sendRawNec(address: Int, command: Int) {
        transmit(IrProtocols.NEC_FREQ, IrProtocols.necAddrCmd(address, command))
    }

    private fun transmit(freq: Int, pattern: IntArray) {
        if (manager == null || !manager.hasIrEmitter()) {
            Toast.makeText(context, "Cet appareil n'a pas d'émetteur infrarouge", Toast.LENGTH_LONG).show()
            return
        }
        try { manager.transmit(freq, pattern) }
        catch (e: Exception) { Toast.makeText(context, "Erreur IR: ${e.message}", Toast.LENGTH_SHORT).show() }
    }
}
