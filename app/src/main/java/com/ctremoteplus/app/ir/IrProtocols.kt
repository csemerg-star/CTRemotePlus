package com.ctremoteplus.app.ir

object IrProtocols {
    const val NEC_FREQ = 38000
    const val SIRC_FREQ = 40000

    fun nec32(codeHex: Long): IntArray {
        val bits = ArrayList<Int>()
        for (i in 31 downTo 0) bits.add(((codeHex shr i) and 1L).toInt())
        return buildNecPattern(bits)
    }

    fun necAddrCmd(address: Int, command: Int): IntArray {
        val bits = ArrayList<Int>()
        fun pushByte(b: Int) { for (i in 0 until 8) bits.add((b shr i) and 1) }
        pushByte(address); pushByte(address.inv() and 0xFF); pushByte(command); pushByte(command.inv() and 0xFF)
        return buildNecPattern(bits)
    }

    private fun buildNecPattern(bits: List<Int>): IntArray {
        val p = ArrayList<Int>()
        p.add(9000); p.add(4500)
        for (b in bits) { p.add(560); p.add(if (b == 1) 1690 else 560) }
        p.add(560)
        return p.toIntArray()
    }

    fun sirc(command: Int, device: Int): IntArray {
        val p = ArrayList<Int>()
        p.add(2400); p.add(600)
        fun emit(value: Int, n: Int) {
            for (i in 0 until n) {
                val bit = (value shr i) and 1
                p.add(if (bit == 1) 1200 else 600); p.add(600)
            }
        }
        emit(command, 7); emit(device, 5)
        return p.toIntArray()
    }
}
