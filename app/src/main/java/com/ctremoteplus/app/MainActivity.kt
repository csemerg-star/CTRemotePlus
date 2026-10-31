package com.ctremoteplus.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ctremoteplus.app.databinding.ActivityMainBinding
import com.ctremoteplus.app.ir.IrCodeDatabase
import com.ctremoteplus.app.ir.IrController
import com.ctremoteplus.app.ir.IrScanner

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var irController: IrController
    private var irScanner: IrScanner? = null
    private val mode = "IR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irController = IrController(this)
        updateHeader()

        binding.btnAddDevice.setOnClickListener {
            startActivity(Intent(this, DeviceSelectionActivity::class.java))
            finish()
        }

        binding.btnBrandMenu.setOnClickListener { showBrandDialog() }

        binding.btnPower.setOnClickListener { sendCommand("POWER") }
        binding.btnVolUp.setOnClickListener { sendCommand("VOL_UP") }
        binding.btnVolDown.setOnClickListener { sendCommand("VOL_DOWN") }
        binding.btnMute.setOnClickListener { sendCommand("MUTE") }
        binding.btnChUp.setOnClickListener { sendCommand("CH_UP") }
        binding.btnChDown.setOnClickListener { sendCommand("CH_DOWN") }
        binding.btnUp.setOnClickListener { sendCommand("UP") }
        binding.btnDown.setOnClickListener { sendCommand("DOWN") }
        binding.btnLeft.setOnClickListener { sendCommand("LEFT") }
        binding.btnRight.setOnClickListener { sendCommand("RIGHT") }
        binding.btnOk.setOnClickListener { sendCommand("OK") }
        binding.btnBack.setOnClickListener { sendCommand("RETURN") }
        binding.btnMenu.setOnClickListener { sendCommand("MENU") }

        // Envoie directement la commande SOURCE a la TV (ouvre le menu sur l'ecran du televiseur)
        binding.btnSource.setOnClickListener {
            sendCommand("SOURCE")
            Toast.makeText(this, "Commande Source envoyée à l'appareil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHeader() {
        val deviceType = Prefs.getDeviceType(this)
        binding.tvDeviceName.text = "$deviceType - ${Prefs.getBrand(this)}"
    }

    private fun resolveIrSet() = if (Prefs.getBrand(this) == "Personnalisé") {
        val addr = Prefs.getCustomAddress(this)
        if (addr >= 0) IrCodeDatabase.buildCustomSet(addr) else null
    } else IrCodeDatabase.BRANDS[Prefs.getBrand(this)]

    private fun sendCommand(key: String) {
        val set = resolveIrSet()
        if (set == null) {
            Toast.makeText(this, "Choisissez une marque (icône en haut à droite)", Toast.LENGTH_SHORT).show()
            return
        }
        irController.sendCommand(set, key)
    }

    private fun showBrandDialog() {
        val items = IrCodeDatabase.BRANDS.keys.toMutableList().apply {
            add("Scanner automatiquement (toute marque)")
        }
        AlertDialog.Builder(this).setTitle("Choisir la marque")
            .setItems(items.toTypedArray()) { _, which ->
                val choice = items[which]
                if (choice == "Scanner automatiquement (toute marque)") {
                    showScanDialog()
                } else {
                    Prefs.setBrand(this, choice)
                    updateHeader()
                    Toast.makeText(this, "Marque sélectionnée : $choice", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    private fun showScanDialog() {
        val statusView = TextView(this).apply {
            text = "Pointez la télécommande vers l'appareil et appuyez sur Démarrer.\nDès qu'il réagit, appuyez sur Ça fonctionne."
            setPadding(40, 20, 40, 20)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Recherche automatique du code")
            .setView(statusView)
            .setPositiveButton("Démarrer", null)
            .setNegativeButton("Annuler") { d, _ -> irScanner?.stop(); d.dismiss() }
            .setNeutralButton("Ça fonctionne !", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                irScanner = IrScanner(irController) { msg -> runOnUiThread { statusView.text = msg } }
                irScanner?.start()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                irScanner?.let {
                    it.stop()
                    Prefs.setCustomAddress(this, it.lastAddress)
                    Prefs.setBrand(this, "Personnalisé")
                    updateHeader()
                    Toast.makeText(this, "Profil sauvegardé (0x${it.lastAddress.toString(16)})", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
