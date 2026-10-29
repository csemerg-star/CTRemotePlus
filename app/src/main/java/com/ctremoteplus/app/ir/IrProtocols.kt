package com.ctremoteplus.app.ir

object IrProtocols {
    const val NEC_FREQ = 38000
    const val SIRC_FREQ = 40000
    const val RC5_FREQ = 36000
    const val KASEIKYO_FREQ = 37000

    fun nec32(codeHex: Long): IntArray {
        val bits = ArrayList<Int>()
        for (i in 31 downTo 0) bits.add(((codeHex shr i) and 1L).toInt())
        return buildNecPattern(bits)
    }

    fun necAddrCmd(address: Int, command: Int, extended: Boolean = false): IntArray {
        val addrLow = address and 0xFF
        val addrHigh = if (extended) (address shr 8) and 0xFF else addrLow.inv() and 0xFF
        val cmd = command and 0xFF
        val cmdInv = cmd.inv() and 0xFF
        val bits = ArrayList<Int>()
        fun pushByte(b: Int) { for (i in 0 until 8) bits.add((b shr i) and 1) }
        pushByte(addrLow); pushByte(addrHigh); pushByte(cmd); pushByte(cmdInv)
        return buildNecPattern(bits)
    }

    private fun buildNecPattern(bits: List<Int>): IntArray {
        val p = ArrayList<Int>()
        p.add(9000); p.add(4500)
        for (b in bits) { p.add(560); p.add(if (b == 1) 1690 else 560) }
        p.add(560)
        return p.toIntArray()
    }

    fun sirc(command: Int, device: Int, bits: Int = 12): IntArray {
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

    fun rc5(address: Int, command: Int, toggle: Int = 0): IntArray {
        val T = 889
        val bits = ArrayList<Int>()
        bits.add(1); bits.add(1); bits.add(toggle)
        for (i in 4 downTo 0) bits.add((address shr i) and 1)
        for (i in 5 downTo 0) bits.add((command shr i) and 1)

        val levels = ArrayList<Boolean>()
        for (b in bits) {
            if (b == 1) { levels.add(false); levels.add(true) }
            else { levels.add(true); levels.add(false) }
        }
        val pulses = ArrayList<Int>()
        var curLevel = levels[0]
        var curDur = T
        for (i in 1 until levels.size) {
            if (levels[i] == curLevel) curDur += T
            else { pulses.add(curDur); curLevel = levels[i]; curDur = T }
        }
        pulses.add(curDur)
        return if (levels[0]) pulses.toIntArray() else intArrayOf(0, *pulses.toIntArray())
    }

    fun kaseikyo(vendorId: Int, genre1: Int, genre2: Int, data: Int, idByte: Int = 0): IntArray {
        val vendorParity = ((vendorId and 0x0F) xor ((vendorId shr 4) and 0x0F) xor
                ((vendorId shr 8) and 0x0F) xor ((vendorId shr 12) and 0x0F))
        val parity = (genre1 xor genre2 xor data xor idByte) and 0xFF

        val bits = ArrayList<Int>()
        fun pushLSB(value: Int, n: Int) { for (i in 0 until n) bits.add((value shr i) and 1) }
        pushLSB(vendorId, 16); pushLSB(vendorParity, 4)
        pushLSB(genre1, 4); pushLSB(genre2, 4)
        pushLSB(data, 8); pushLSB(idByte, 8); pushLSB(parity, 8)

        val p = ArrayList<Int>()
        p.add(3500); p.add(1750)
        for (b in bits) { p.add(420); p.add(if (b == 1) 1300 else 420) }
        p.add(420)
        return p.toIntArray()
    }
}
