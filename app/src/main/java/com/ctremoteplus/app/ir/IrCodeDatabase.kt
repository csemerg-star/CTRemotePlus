package com.ctremoteplus.app.ir

data class IrCodeSet(
    val protocol: String,
    val codes: Map<String, Long>,
    val address: Int? = null,
    val vendorId: Int? = null,
    val inputCycleKey: String = "SOURCE",
    val inputOrder: List<String> = listOf("TV","AV","Component","HDMI1","HDMI2","HDMI3","USB")
)

object IrCodeDatabase {

    val SAMSUNG = IrCodeSet("NEC32", mapOf(
        "POWER" to 0xE0E040BFL, "VOL_UP" to 0xE0E0E01FL, "VOL_DOWN" to 0xE0E0D02FL,
        "MUTE" to 0xE0E0F00FL, "CH_UP" to 0xE0E048B7L, "CH_DOWN" to 0xE0E008F7L,
        "SOURCE" to 0xE0E0807FL, "MENU" to 0xE0E058A7L, "RETURN" to 0xE0E01AE5L,
        "HOME" to 0xE0E09E61L, "UP" to 0xE0E006F9L, "DOWN" to 0xE0E08679L,
        "LEFT" to 0xE0E0A659L, "RIGHT" to 0xE0E046B9L, "OK" to 0xE0E016E9L,
        "0" to 0xE0E08877L, "1" to 0xE0E020DFL, "2" to 0xE0E0A05FL, "3" to 0xE0E0609FL,
        "4" to 0xE0E010EFL, "5" to 0xE0E0906FL, "6" to 0xE0E050AFL, "7" to 0xE0E030CFL,
        "8" to 0xE0E0B04FL, "9" to 0xE0E0708FL, "LAST" to 0xE0E0C837L
    ))

    val LG = IrCodeSet("NEC32", mapOf(
        "POWER" to 0x20DF10EFL, "VOL_UP" to 0x20DF40BFL, "VOL_DOWN" to 0x20DFC03FL,
        "MUTE" to 0x20DF906FL, "CH_UP" to 0x20DF00FFL, "CH_DOWN" to 0x20DF807FL,
        "SOURCE" to 0x20DFD02FL, "MENU" to 0x20DFC23DL, "RETURN" to 0x20DF14EBL,
        "HOME" to 0x20DF3EC1L, "UP" to 0x20DF02FDL, "DOWN" to 0x20DF827DL,
        "LEFT" to 0x20DFE01FL, "RIGHT" to 0x20DF609FL, "OK" to 0x20DF22DDL,
        "0" to 0x20DF08F7L, "1" to 0x20DF8877L, "2" to 0x20DF48B7L, "3" to 0x20DFC837L,
        "4" to 0x20DF28D7L, "5" to 0x20DFA857L, "6" to 0x20DF6897L, "7" to 0x20DFE817L,
        "8" to 0x20DF18E7L, "9" to 0x20DF9867L, "LAST" to 0x20DF52ADL
    ))

    val SONY = IrCodeSet("SIRC12", mapOf(
        "POWER" to 21L, "VOL_UP" to 18L, "VOL_DOWN" to 19L, "MUTE" to 20L,
        "CH_UP" to 16L, "CH_DOWN" to 17L, "SOURCE" to 37L, "MENU" to 26L,
        "UP" to 116L, "DOWN" to 117L, "LEFT" to 52L, "RIGHT" to 51L, "OK" to 101L,
        "0" to 9L, "1" to 0L, "2" to 1L, "3" to 2L, "4" to 3L, "5" to 4L,
        "6" to 5L, "7" to 6L, "8" to 7L, "9" to 8L
    ))

    val PHILIPS = IrCodeSet("RC5", mapOf(
        "POWER" to 12L, "MUTE" to 13L, "VOL_UP" to 16L, "VOL_DOWN" to 17L,
        "CH_UP" to 32L, "CH_DOWN" to 33L,
        "0" to 0L, "1" to 1L, "2" to 2L, "3" to 3L, "4" to 4L,
        "5" to 5L, "6" to 6L, "7" to 7L, "8" to 8L, "9" to 9L
    ), address = 0)

    val PANASONIC = IrCodeSet("KASEIKYO", mapOf(
        "POWER" to 0x3DL
    ), vendorId = 0x2002)

    private val GENERIC_CMD_MAP = mapOf(
        "POWER" to 0x02, "VOL_UP" to 0x1A, "VOL_DOWN" to 0x1E, "MUTE" to 0x0D,
        "CH_UP" to 0x22, "CH_DOWN" to 0x26, "SOURCE" to 0x1B, "MENU" to 0x1F,
        "UP" to 0x06, "DOWN" to 0x16, "LEFT" to 0x0A, "RIGHT" to 0x12, "OK" to 0x1C,
        "HOME" to 0x5B, "RETURN" to 0x45,
        "0" to 0x44, "1" to 0x4E, "2" to 0x4A, "3" to 0x56, "4" to 0x52,
        "5" to 0x5E, "6" to 0x4F, "7" to 0x43, "8" to 0x53, "9" to 0x5F
    )
    private fun genericSet(addr: Int) = IrCodeSet(
        "NEC_ADDR", GENERIC_CMD_MAP.mapValues { it.value.toLong() }, address = addr
    )

    val TOSHIBA = genericSet(0x40)
    val SHARP = genericSet(0x5A)
    val HISENSE = genericSet(0x3D)
    val TCL = genericSet(0xA8)
    val VIZIO = genericSet(0x7E)
    val JVC = genericSet(0x03)
    val SANYO = genericSet(0x6B)
    val THOMSON = genericSet(0x0A)
    val HAIER = genericSet(0x50)
    val CHANGHONG = genericSet(0x6F)
    val SKYWORTH = genericSet(0x74)
    val KONKA = genericSet(0x66)
    val AOC_ELEMENT_WESTINGHOUSE_RCA = genericSet(0x87)

    fun buildCustomSet(address: Int): IrCodeSet = genericSet(address)

    val GENERIC_NEC_ADDRESSES = listOf(
        0x00,0x01,0x02,0x03,0x04,0x07,0x08,0x0A,0x0B,0x0C,0x0F,
        0x10,0x20,0x3D,0x40,0x43,0x4B,0x50,0x57,0x59,0x5A,0x5B,
        0x61,0x62,0x65,0x66,0x6B,0x6F,0x74,0x76,0x7E,0x80,0x87,
        0x97,0xA2,0xA8,0xAE,0xBC,0xC1,0xE0
    )
    val GENERIC_POWER_COMMANDS = listOf(0x02,0x0C,0x10,0x12,0x15,0x16,0x1C,0x4D,0x5C)

    val BRANDS = linkedMapOf(
        "Samsung" to SAMSUNG,
        "LG" to LG,
        "Sony" to SONY,
        "Philips / Grundig / Telefunken" to PHILIPS,
        "Panasonic (Viera)" to PANASONIC,
        "Toshiba" to TOSHIBA,
        "Sharp" to SHARP,
        "Hisense" to HISENSE,
        "TCL" to TCL,
        "Vizio" to VIZIO,
        "JVC" to JVC,
        "Sanyo" to SANYO,
        "Thomson" to THOMSON,
        "Haier" to HAIER,
        "Changhong" to CHANGHONG,
        "Skyworth" to SKYWORTH,
        "Konka" to KONKA,
        "AOC / Element / Westinghouse / RCA" to AOC_ELEMENT_WESTINGHOUSE_RCA
    )
}
