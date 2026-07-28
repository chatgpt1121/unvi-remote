package com.masterremote.app

/**
 * IrEngine
 * ---------
 * Generates raw IR pulse patterns for the NEC protocol (used by ~80% of
 * TVs, ACs and set-top boxes sold in India: Samsung, LG, TCL, Panasonic,
 * MI, most STBs, most window/split AC remotes).
 *
 * Sony TVs use a different protocol (SIRC, 12/15/20-bit) which is handled
 * separately below.
 *
 * IMPORTANT (read this before shipping to 100+ real devices):
 * The address/command pairs in IrCodeDatabase.kt are the MOST COMMONLY
 * published codes for each brand's power button, gathered from open
 * remote-control databases (LIRC, Flipper-IRDB, TV-B-Gone project).
 * They cover the majority of models but NOT every single model/year.
 * Treat this file as a strong starting point, not a 100%-guaranteed list.
 * See the README for how to add/verify more codes for the exact models
 * you have on site.
 */
object IrEngine {

    private const val NEC_FREQ = 38000

    /** Builds a raw NEC pulse pattern (microseconds) from an 8-bit address + 8-bit command. */
    fun necPattern(address: Int, command: Int): IntArray {
        val addrInv = address.inv() and 0xFF
        val cmdInv = command.inv() and 0xFF
        val bits = mutableListOf<Int>()

        fun addByte(byte: Int) {
            for (i in 0 until 8) {
                bits.add((byte shr i) and 1)
            }
        }
        addByte(address)
        addByte(addrInv)
        addByte(command)
        addByte(cmdInv)

        val pattern = mutableListOf<Int>()
        // Leading burst
        pattern.add(9000); pattern.add(4500)
        for (bit in bits) {
            pattern.add(560)
            pattern.add(if (bit == 1) 1690 else 560)
        }
        // Trailing burst
        pattern.add(560)
        return pattern.toIntArray()
    }

    /** Builds a raw Sony SIRC pulse pattern (12-bit: 7 command bits + 5 address bits). */
    fun sirc12Pattern(address: Int, command: Int): IntArray {
        val pattern = mutableListOf<Int>()
        pattern.add(2400); pattern.add(600)
        for (i in 0 until 7) {
            val bit = (command shr i) and 1
            pattern.add(if (bit == 1) 1200 else 600)
            pattern.add(600)
        }
        for (i in 0 until 5) {
            val bit = (address shr i) and 1
            pattern.add(if (bit == 1) 1200 else 600)
            pattern.add(600)
        }
        return pattern.toIntArray()
    }

    fun frequencyFor(protocol: String): Int = when (protocol) {
        "SIRC" -> 40000
        else -> NEC_FREQ
    }
}
