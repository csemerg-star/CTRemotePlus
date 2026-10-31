package com.ctremoteplus.app.ir
object IrProtocols {
    const val NEC_FREQ = 38000
    fun nec32(codeHex: Long): IntArray {
        val bits = ArrayList<Int>()
        for (i in 31 downTo 0) bits.add(((codeHex shr i) and 1L).toInt())
        val p = ArrayList<Int>(); p.add(9000); p.add(4500)
        for (b in bits) { p.add(560); p.add(if (b == 1) 1690 else 560) }
        p.add(560); return p.toIntArray()
    }
    fun necAddrCmd(address: Int, command: Int): IntArray {
        val bits = ArrayList<Int>()
        fun push(b: Int) { for (i in 0 until 8) bits.add((b shr i) and 1) }
        push(address); push(address.inv() and 0xFF); push(command); push(command.inv() and 0xFF)
        val p = ArrayList<Int>(); p.add(9000); p.add(4500)
        for (b in bits) { p.add(560); p.add(if (b == 1) 1690 else 560) }
        p.add(560); return p.toIntArray()
    }
    fun rc5(address: Int, command: Int): IntArray { return necAddrCmd(address, command) }
}
