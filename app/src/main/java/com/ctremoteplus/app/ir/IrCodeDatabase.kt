package com.ctremoteplus.app.ir

data class IrCodeSet(
    val protocol: String,
    val codes: Map<String, Long>,
    val address: Int? = null
)

object IrCodeDatabase {

    val SAMSUNG = IrCodeSet("NEC32", mapOf(
        "POWER" to 0xE0E040BFL, "VOL_UP" to 0xE0E0E01FL, "VOL_DOWN" to 0xE0E0D02FL,
        "MUTE" to 0xE0E0F00FL, "CH_UP" to 0xE0E048B7L, "CH_DOWN" to 0xE0E008F7L,
        "SOURCE" to 0xE0E0807FL, "MENU" to 0xE0E058A7L, "RETURN" to 0xE0E01AE5L,
        "UP" to 0xE0E006F9L, "DOWN" to 0xE0E08679L, "LEFT" to 0xE0E0A659L,
        "RIGHT" to 0xE0E046B9L, "OK" to 0xE0E016E9L
    ))

    val LG = IrCodeSet("NEC32", mapOf(
        "POWER" to 0x20DF10EFL, "VOL_UP" to 0x20DF40BFL, "VOL_DOWN" to 0x20DFC03FL,
        "MUTE" to 0x20DF906FL, "CH_UP" to 0x20DF00FFL, "CH_DOWN" to 0x20DF807FL,
        "SOURCE" to 0x20DFD02FL, "MENU" to 0x20DFC23DL, "RETURN" to 0x20DF14EBL,
        "UP" to 0x20DF02FDL, "DOWN" to 0x20DF827DL, "LEFT" to 0x20DFE01FL,
        "RIGHT" to 0x20DF609FL, "OK" to 0x20DF22DDL
    ))

    val SONY = IrCodeSet("SIRC12", mapOf(
        "POWER" to 21L, "VOL_UP" to 18L, "VOL_DOWN" to 19L, "MUTE" to 20L,
        "CH_UP" to 16L, "CH_DOWN" to 17L, "SOURCE" to 37L, "MENU" to 26L,
        "UP" to 116L, "DOWN" to 117L, "LEFT" to 52L, "RIGHT" to 51L, "OK" to 101L
    ))

    private val GENERIC_CMD_MAP = mapOf(
        "POWER" to 0x02, "VOL_UP" to 0x1A, "VOL_DOWN" to 0x1E, "MUTE" to 0x0D,
        "CH_UP" to 0x22, "CH_DOWN" to 0x26, "SOURCE" to 0x1B, "MENU" to 0x1F,
        "UP" to 0x06, "DOWN" to 0x16, "LEFT" to 0x0A, "RIGHT" to 0x12,
        "OK" to 0x1C, "RETURN" to 0x45
    )
    private fun genericSet(addr: Int) = IrCodeSet(
        "NEC_ADDR", GENERIC_CMD_MAP.mapValues { it.value.toLong() }, address = addr
    )

    val PHILIPS = genericSet(0x00)
    val PANASONIC = genericSet(0x02)
    val TOSHIBA = genericSet(0x40)
    val SHARP = genericSet(0x5A)
    val HISENSE = genericSet(0x3D)
    val TCL = genericSet(0xA8)

    fun buildCustomSet(address: Int): IrCodeSet = genericSet(address)

    val GENERIC_NEC_ADDRESSES = listOf(
        0x00,0x01,0x02,0x03,0x04,0x07,0x08,0x0A,0x0B,0x0C,0x0F,
        0x10,0x20,0x3D,0x40,0x43,0x4B,0x50,0x57,0x59,0x5A,0x5B,
        0x61,0x62,0x65,0x76,0x80,0x87,0x97,0xA2,0xA8,0xAE,0xBC,0xC1,0xE0
    )
    val GENERIC_POWER_COMMANDS = listOf(0x02,0x0C,0x10,0x12,0x15,0x16,0x1C,0x4D,0x5C)

    val BRANDS = linkedMapOf(
        "Samsung" to SAMSUNG,
        "LG" to LG,
        "Sony" to SONY,
        "Philips" to PHILIPS,
        "Panasonic" to PANASONIC,
        "Toshiba" to TOSHIBA,
        "Sharp" to SHARP,
        "Hisense" to HISENSE,
        "TCL" to TCL
    )
}
