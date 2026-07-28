package com.masterremote.app

data class IrCode(
    val brand: String,
    val protocol: String,   // "NEC" or "SIRC"
    val address: Int,
    val command: Int
)

/**
 * Starter database of POWER (on/off toggle) codes for brands most common
 * in Indian homes/offices. Grouped as TV and AC so the app can show two
 * separate tabs, and so the "Turn Everything Off" button can loop through
 * TV codes and AC codes together or separately.
 *
 * Add more brands/models by appending entries here — address/command pairs
 * can be sourced from:
 *   - LIRC remote database: https://lirc-remotes.sourceforge.io/
 *   - Flipper-IRDB (GitHub, open source): search "Flipper-IRDB"
 *   - SmartIR project (GitHub, open source): search "smartir codes"
 */
object IrCodeDatabase {

    val TV_CODES = listOf(
        IrCode("Samsung",   "NEC",  0x07, 0x02),
        IrCode("LG",        "NEC",  0x04, 0x08),
        IrCode("LG (alt)",  "NEC",  0x00, 0x8B),
        IrCode("Sony",      "SIRC", 0x01, 0x15),
        IrCode("Panasonic", "NEC",  0x40, 0x3D),
        IrCode("TCL",       "NEC",  0x00, 0x0C),
        IrCode("MI/Xiaomi", "NEC",  0x66, 0x0C),
        IrCode("VU",        "NEC",  0x00, 0x0C),
        IrCode("Onida",     "NEC",  0x08, 0x45),
        IrCode("Videocon",  "NEC",  0x08, 0x45),
        IrCode("Haier",     "NEC",  0x00, 0x12),
        IrCode("Thomson",   "NEC",  0x00, 0x0C)
    )

    // NOTE: AC codes are NOT here anymore. Unlike TVs, AC remotes use raw,
    // model-specific pulse timings (not a clean address/command pair), so
    // they live separately in AcCodeDatabase.kt as real captured signals
    // (sourced from the open-source SmartIR project). See that file for
    // LG_OFF_CODES and SAMSUNG_OFF_CODES.
    // Voltas, Daikin, Hitachi, Blue Star, Carrier are NOT included yet —
    // add real captured codes for them the same way (see README).
}
