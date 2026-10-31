package com.ctremoteplus.app.ir
data class IrCodeSet(val protocol: String, val codes: Map<String, Long>, val address: Int? = null)
object IrCodeDatabase {
    val SAMSUNG = IrCodeSet("NEC32", mapOf("POWER" to 0xE0E040BFL, "VOL_UP" to 0xE0E0E01FL, "VOL_DOWN" to 0xE0E0D02FL, "MUTE" to 0xE0E0F00FL, "CH_UP" to 0xE0E048B7L, "CH_DOWN" to 0xE0E008F7L, "SOURCE" to 0xE0E0807FL, "MENU" to 0xE0E058A7L, "RETURN" to 0xE0E01AE5L, "UP" to 0xE0E006F9L, "DOWN" to 0xE0E08679L, "LEFT" to 0xE0E0A659L, "RIGHT" to 0xE0E046B9L, "OK" to 0xE0E016E9L))
    val LG = IrCodeSet("NEC32", mapOf("POWER" to 0x20DF10EFL, "VOL_UP" to 0x20DF40BFL, "VOL_DOWN" to 0x20DFC03FL, "MUTE" to 0x20DF906FL, "CH_UP" to 0x20DF00FFL, "CH_DOWN" to 0x20DF807FL, "SOURCE" to 0x20DFD02FL, "MENU" to 0x20DFC23DL, "RETURN" to 0x20DF14EBL, "UP" to 0x20DF02FDL, "DOWN" to 0x20DF827DL, "LEFT" to 0x20DFE01FL, "RIGHT" to 0x20DF609FL, "OK" to 0x20DF22DDL))
    val BRANDS = linkedMapOf("Samsung" to SAMSUNG, "LG" to LG)
}
