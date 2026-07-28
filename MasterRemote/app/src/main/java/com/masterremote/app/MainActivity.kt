package com.masterremote.app

import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private lateinit var irManager: ConsumerIrManager
    private lateinit var statusText: TextView

    // Gap between each brute-force blast so devices don't miss the signal
    private val GAP_MS = 400L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        irManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager

        if (!irManager.hasIrEmitter()) {
            statusText.text = "⚠️ Is phone mein IR blaster nahi mila.\n" +
                    "Ye app sirf un phones pe kaam karega jinme built-in IR blaster ho."
            findViewById<Button>(R.id.btnAllOff).isEnabled = false
            findViewById<Button>(R.id.btnTvOff).isEnabled = false
            findViewById<Button>(R.id.btnAcOff).isEnabled = false
            return
        }

        statusText.text = "✅ IR blaster mil gaya. Ready."

        findViewById<Button>(R.id.btnTvOff).setOnClickListener {
            bruteForceOff(IrCodeDatabase.TV_CODES, "TVs")
        }

        findViewById<Button>(R.id.btnAcOff).setOnClickListener {
            bruteForceAcOff()
        }

        findViewById<Button>(R.id.btnAllOff).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                bruteForceOffSuspend(IrCodeDatabase.TV_CODES, "TVs")
                bruteForceAcOffSuspend()
                statusText.text = "✅ Sab TV + AC — saare known codes try ho gaye."
                Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Fires every known TV power code in the list, one after another, with a
     * small gap between each. This is the "brute force" approach: instead
     * of asking which brand, it just tries all of them very fast so any
     * TV in range that matches one of the codes turns off.
     */
    private fun bruteForceOff(codes: List<IrCode>, label: String) {
        statusText.text = "$label band karne ki koshish ho rahi hai..."
        CoroutineScope(Dispatchers.Main).launch {
            bruteForceOffSuspend(codes, label)
            statusText.text = "✅ $label — saare known codes try ho gaye."
            Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun bruteForceOffSuspend(codes: List<IrCode>, label: String) {
        for ((index, code) in codes.withIndex()) {
            statusText.text = "Trying ${code.brand} (${index + 1}/${codes.size})..."
            transmit(code)
            delay(GAP_MS)
        }
    }

    /**
     * AC codes are raw pulse patterns (real captured signals), not
     * address/command pairs, so they use a separate transmit path.
     */
    private fun bruteForceAcOff() {
        statusText.text = "AC band karne ki koshish ho rahi hai..."
        CoroutineScope(Dispatchers.Main).launch {
            bruteForceAcOffSuspend()
            statusText.text = "✅ AC — saare known LG/Samsung codes try ho gaye."
            Toast.makeText(this@MainActivity, "Done", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun bruteForceAcOffSuspend() {
        val allAcCodes = AcCodeDatabase.LG_OFF_CODES + AcCodeDatabase.SAMSUNG_OFF_CODES
        for ((index, code) in allAcCodes.withIndex()) {
            statusText.text = "Trying ${code.label} (${index + 1}/${allAcCodes.size})..."
            transmitRaw(code.pattern)
            delay(GAP_MS)
        }
    }

    private fun transmit(code: IrCode) {
        val pattern = when (code.protocol) {
            "SIRC" -> IrEngine.sirc12Pattern(code.address, code.command)
            else -> IrEngine.necPattern(code.address, code.command)
        }
        val freq = IrEngine.frequencyFor(code.protocol)
        try {
            irManager.transmit(freq, pattern)
        } catch (e: Exception) {
            // Some phones throttle rapid IR transmits — safe to ignore and continue
        }
    }

    private fun transmitRaw(pattern: IntArray) {
        try {
            irManager.transmit(38000, pattern)
        } catch (e: Exception) {
            // Some phones throttle rapid IR transmits — safe to ignore and continue
        }
    }
}
