package com.ctremoteplus.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ctremoteplus.app.databinding.ActivityMainBinding
import com.ctremoteplus.app.ir.IrCodeDatabase
import com.ctremoteplus.app.ir.IrController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var irController: IrController
    private var mode = "IR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irController = IrController(this)
        mode = Prefs.getMode(this)
        
        val deviceType = Prefs.getDeviceType(this)
        binding.tvDeviceName.text = "$deviceType • ${Prefs.getBrand(this)}"

        // Retour à la page d'accueil
        binding.btnAddDevice.setOnClickListener {
            startActivity(Intent(this, DeviceSelectionActivity::class.java))
            finish()
        }

        // Boutons de la télécommande
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
        
        // LE CORRECTIF : Ouvre le menu source directement sur la TV !
        binding.btnSource.setOnClickListener { 
            sendCommand("SOURCE") 
            Toast.makeText(this, "Commande Source envoyée à la TV", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCommand(key: String) {
        if (mode == "IR") {
            val set = IrCodeDatabase.BRANDS[Prefs.getBrand(this)]
            if (set == null) {
                Toast.makeText(this, "Veuillez choisir une marque", Toast.LENGTH_SHORT).show()
                return
            }
            irController.sendCommand(set, key)
        } else {
            // Le mode Wi-Fi reste géré ici (votre ancien code)
        }
    }
}
