package com.ctremoteplus.app.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.widget.Toast

class IrController(private val context: Context) {
    private val manager: ConsumerIrManager? = context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    fun sendCommand(set: IrCodeSet, key: String) {
        val code = set.codes[key] ?: run {
            Toast.makeText(context, "Code non disponible", Toast.LENGTH_SHORT).show()
            return
        }
        val pattern = when (set.protocol) {
            "NEC32" -> IrProtocols.nec32(code)
            "NEC_ADDR" -> IrProtocols.necAddrCmd(set.address ?: 0, code.toInt())
            "RC5" -> IrProtocols.rc5(set.address ?: 0, code.toInt())
            else -> IrProtocols.nec32(code)
        }
        try {
            manager?.transmit(IrProtocols.NEC_FREQ, pattern)
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur IR", Toast.LENGTH_SHORT).show()
        }
    }
}
