package com.ctremoteplus.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ctremoteplus.app.databinding.ActivityMainBinding
import com.ctremoteplus.app.ir.IrCodeDatabase
import com.ctremoteplus.app.ir.IrCodeSet
import com.ctremoteplus.app.ir.IrController
import com.ctremoteplus.app.ir.IrScanner
import com.ctremoteplus.app.net.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var irController: IrController
    private var samsungController: SamsungTvController? = null
    private var lgController: LgTvController? = null
    private var adbController: AndroidTvAdbController? = null
    private var irScanner: IrScanner? = null
    private var mode = "IR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        irController = IrController(this)
        mode = Prefs.getMode(this)
        updateModeUI()
        buildNumPad()

        binding.btnModeIr.setOnClickListener { mode = "IR"; Prefs.setMode(this, mode); updateModeUI() }
        binding.btnModeWifi.setOnClickListener { mode = "WIFI"; Prefs.setMode(this, mode); updateModeUI() }
        binding.btnBrand.setOnClickListener { showBrandDialog() }
        binding.btnSettings.setOnClickListener { showWifiSetupDialog() }
        binding.btnSource.setOnClickListener { showSourceDialog() }

        binding.btnPower.setOnClickListener { sendCommand("POWER") }
        binding.btnVolUp.setOnClickListener { sendCommand("VOL_UP") }
        binding.btnVolDown.setOnClickListener { sendCommand("VOL_DOWN") }
        binding.btnMute.setOnClickListener { sendCommand("MUTE") }
        binding.btnChUp.setOnClickListener { sendCommand("CH_UP") }
        binding.btnChDown.setOnClickListener { sendCommand("CH_DOWN") }
        binding.btnLast.setOnClickListener { sendCommand("LAST") }
        binding.btnUp.setOnClickListener { sendCommand("UP") }
        binding.btnDown.setOnClickListener { sendCommand("DOWN") }
        binding.btnLeft.setOnClickListener { sendCommand("LEFT") }
        binding.btnRight.setOnClickListener { sendCommand("RIGHT") }
        binding.btnOk.setOnClickListener { sendCommand("OK") }
        binding.btnBack.setOnClickListener { sendCommand("RETURN") }
        binding.btnHome.setOnClickListener { sendCommand("HOME") }
        binding.btnMenu.setOnClickListener { sendCommand("MENU") }

        updateDeviceLabel()
    }

    private fun updateModeUI() {
        binding.btnModeIr.backgroundTintList = getColorStateList(if (mode == "IR") R.color.brown else R.color.navy_light)
        binding.btnModeWifi.backgroundTintList = getColorStateList(if (mode == "WIFI") R.color.brown else R.color.navy_light)
        updateDeviceLabel()
    }

    private fun updateDeviceLabel() {
        binding.tvDeviceName.text = if (mode == "IR") "IR - ${Prefs.getBrand(this)}" else "Wi-Fi - ${Prefs.getWifiBrand(this)}"
    }

    private fun buildNumPad() {
        val grid = binding.numPad
        grid.removeAllViews(); grid.columnCount = 3
        listOf("1","2","3","4","5","6","7","8","9","","0","").forEach { label ->
            val btn = Button(this).apply {
                text = label
                setTextColor(getColor(R.color.text_white))
                background = getDrawable(R.drawable.btn_round)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0; height = 110
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(6,6,6,6)
                }
                setOnClickListener { if (label.isNotEmpty()) sendCommand(label) }
            }
            grid.addView(btn)
        }
    }

    private fun resolveIrSet(): IrCodeSet? {
        val brand = Prefs.getBrand(this)
        if (brand == "Personnalise (detecte)") {
            val addr = Prefs.getCustomAddress(this)
            return if (addr >= 0) IrCodeDatabase.buildCustomSet(addr) else null
        }
        return IrCodeDatabase.BRANDS[brand]
    }

    private fun sendCommand(key: String) {
        if (mode == "IR") {
            val set = resolveIrSet()
            if (set == null) { Toast.makeText(this, "Choisissez une marque ou utilisez le Scan", Toast.LENGTH_SHORT).show(); return }
            irController.sendCommand(set, key)
        } else sendWifiCommand(key)
    }

    private fun sendWifiCommand(key: String) {
        val ip = Prefs.getDeviceIp(this)
        if (ip.isEmpty()) { Toast.makeText(this, "Configurez l'IP de la TV", Toast.LENGTH_SHORT).show(); return }
        when (Prefs.getWifiBrand(this)) {
            "Samsung (Tizen)" -> {
                if (samsungController == null) samsungController = SamsungTvController(ip)
                samsungController?.connect { samsungController?.sendKey(mapToSamsungKey(key)) }
            }
            "LG (webOS)" -> {
                if (lgController == null) {
                    val saved = Prefs.prefs(this).getString("lg_client_key", null)
                    lgController = LgTvController(ip, saved) { k -> Prefs.prefs(this).edit().putString("lg_client_key", k).apply() }
                }
                lgController?.connect { lgController?.sendButton(mapToLgKey(key)) }
            }
            "Android TV (ADB)" -> {
                if (adbController == null) adbController = AndroidTvAdbController(ip, File(filesDir, "adbkeys").apply { mkdirs() })
                adbController?.sendKeyEvent(mapToAndroidKeycode(key))
            }
        }
    }

    private fun mapToSamsungKey(key: String) = when (key) {
        "POWER" -> "KEY_POWER"; "VOL_UP" -> "KEY_VOLUP"; "VOL_DOWN" -> "KEY_VOLDOWN"
        "MUTE" -> "KEY_MUTE"; "CH_UP" -> "KEY_CHUP"; "CH_DOWN" -> "KEY_CHDOWN"
        "UP" -> "KEY_UP"; "DOWN" -> "KEY_DOWN"; "LEFT" -> "KEY_LEFT"; "RIGHT" -> "KEY_RIGHT"
        "OK" -> "KEY_ENTER"; "RETURN" -> "KEY_RETURN"; "HOME" -> "KEY_HOME"; "MENU" -> "KEY_MENU"
        "LAST" -> "KEY_PRECH"; else -> "KEY_$key"
    }

    private fun mapToLgKey(key: String) = when (key) {
        "POWER" -> "POWER"; "VOL_UP" -> "VOLUMEUP"; "VOL_DOWN" -> "VOLUMEDOWN"; "MUTE" -> "MUTE"
        "CH_UP" -> "CHANNELUP"; "CH_DOWN" -> "CHANNELDOWN"; "UP" -> "UP"; "DOWN" -> "DOWN"
        "LEFT" -> "LEFT"; "RIGHT" -> "RIGHT"; "OK" -> "ENTER"; "RETURN" -> "BACK"
        "HOME" -> "HOME"; "MENU" -> "MENU"; else -> key
    }

    private fun mapToAndroidKeycode(key: String) = when (key) {
        "POWER" -> "26"; "VOL_UP" -> "24"; "VOL_DOWN" -> "25"; "MUTE" -> "164"
        "CH_UP" -> "166"; "CH_DOWN" -> "167"; "UP" -> "19"; "DOWN" -> "20"
        "LEFT" -> "21"; "RIGHT" -> "22"; "OK" -> "23"; "RETURN" -> "4"
        "HOME" -> "3"; "MENU" -> "82"
        "0" -> "7"; "1" -> "8"; "2" -> "9"; "3" -> "10"; "4" -> "11"
        "5" -> "12"; "6" -> "13"; "7" -> "14"; "8" -> "15"; "9" -> "16"; else -> "0"
    }

    private fun showBrandDialog() {
        val items = mutableListOf<String>().apply {
            addAll(IrCodeDatabase.BRANDS.keys)
            add("Personnalise (detecte)")
            add("Scanner automatiquement (toute marque)")
            add("Wi-Fi : Samsung (Tizen)")
            add("Wi-Fi : LG (webOS)")
            add("Wi-Fi : Android TV (ADB)")
        }
        AlertDialog.Builder(this).setTitle("Choisir la marque / l'appareil")
            .setItems(items.toTypedArray()) { _, which ->
                when (val choice = items[which]) {
                    "Wi-Fi : Samsung (Tizen)" -> { mode = "WIFI"; Prefs.setMode(this, mode); Prefs.setWifiBrand(this, "Samsung (Tizen)") }
                    "Wi-Fi : LG (webOS)" -> { mode = "WIFI"; Prefs.setMode(this, mode); Prefs.setWifiBrand(this, "LG (webOS)") }
                    "Wi-Fi : Android TV (ADB)" -> { mode = "WIFI"; Prefs.setMode(this, mode); Prefs.setWifiBrand(this, "Android TV (ADB)") }
                    "Scanner automatiquement (toute marque)" -> showScanDialog()
                    else -> { mode = "IR"; Prefs.setMode(this, mode); Prefs.setBrand(this, choice) }
                }
                updateModeUI()
            }.show()
    }

    private fun showScanDialog() {
        val statusView = TextView(this).apply {
            text = "Pointez la telecommande vers le televiseur et appuyez sur Demarrer.\nDes que le televiseur reagit, appuyez sur Ca fonctionne."
            setPadding(40, 20, 40, 20)
            setTextColor(getColor(R.color.text_white))
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Recherche automatique du code TV")
            .setView(statusView)
            .setPositiveButton("Demarrer", null)
            .setNegativeButton("Annuler") { d, _ -> irScanner?.stop(); d.dismiss() }
            .setNeutralButton("Ca fonctionne !", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                irScanner = IrScanner(irController) { msg, _, _ ->
                    runOnUiThread { statusView.text = msg }
                }
                irScanner?.start()
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                val scanner = irScanner
                if (scanner != null) {
                    scanner.stop()
                    Prefs.setCustomAddress(this, scanner.lastAddress)
                    Prefs.setBrand(this, "Personnalise (detecte)")
                    mode = "IR"; Prefs.setMode(this, mode); updateModeUI()
                    Toast.makeText(this, "Profil sauvegarde (adresse 0x${scanner.lastAddress.toString(16)})", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showSourceDialog() {
        when {
            mode == "WIFI" && Prefs.getWifiBrand(this) == "Samsung (Tizen)" -> {
                val sources = listOf("HDMI1","HDMI2","HDMI3","HDMI4","AV1","COMPONENT1","USB")
                AlertDialog.Builder(this).setTitle("Choisir la source")
                    .setItems(sources.toTypedArray()) { _, i ->
                        val ip = Prefs.getDeviceIp(this)
                        if (ip.isEmpty()) { Toast.makeText(this,"Configurez l'IP", Toast.LENGTH_SHORT).show(); return@setItems }
                        if (samsungController == null) samsungController = SamsungTvController(ip)
                        samsungController?.connect { samsungController?.switchSource(sources[i]) }
                    }.show()
            }
            mode == "WIFI" && Prefs.getWifiBrand(this) == "LG (webOS)" -> {
                val sources = listOf("HDMI_1","HDMI_2","HDMI_3","HDMI_4","AV_1","USB_1")
                AlertDialog.Builder(this).setTitle("Choisir la source")
                    .setItems(sources.toTypedArray()) { _, i ->
                        val ip = Prefs.getDeviceIp(this)
                        if (ip.isEmpty()) { Toast.makeText(this,"Configurez l'IP", Toast.LENGTH_SHORT).show(); return@setItems }
                        if (lgController == null) {
                            val saved = Prefs.prefs(this).getString("lg_client_key", null)
                            lgController = LgTvController(ip, saved) { k -> Prefs.prefs(this).edit().putString("lg_client_key", k).apply() }
                        }
                        lgController?.connect { lgController?.switchInput(sources[i]) }
                    }.show()
            }
            mode == "WIFI" && Prefs.getWifiBrand(this) == "Android TV (ADB)" -> {
                val ip = Prefs.getDeviceIp(this)
                if (ip.isEmpty()) { Toast.makeText(this,"Configurez l'IP", Toast.LENGTH_SHORT).show(); return }
                if (adbController == null) adbController = AndroidTvAdbController(ip, File(filesDir, "adbkeys").apply { mkdirs() })
                adbController?.cycleInput()
                Toast.makeText(this, "Changement de source envoye", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val set = resolveIrSet()
                if (set == null) { Toast.makeText(this, "Selectionnez une marque d'abord", Toast.LENGTH_SHORT).show(); return }
                AlertDialog.Builder(this).setTitle("Choisir la source (cycle)")
                    .setItems(set.inputOrder.toTypedArray()) { _, i ->
                        val presses = i + 1
                        repeat(presses) { irController.sendCommand(set, set.inputCycleKey) }
                        Toast.makeText(this, "Source : ${set.inputOrder[i]} (${presses}x)", Toast.LENGTH_SHORT).show()
                    }.show()
            }
        }
    }

    private fun showWifiSetupDialog() {
        val input = EditText(this).apply { hint = "Adresse IP de la TV"; setText(Prefs.getDeviceIp(this@MainActivity)) }
        val macInput = EditText(this).apply { hint = "Adresse MAC (pour allumage Wi-Fi)"; setText(Prefs.getDeviceMac(this@MainActivity)) }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(40,20,40,20)
            addView(input); addView(macInput)
        }
        AlertDialog.Builder(this).setTitle("Configuration Wi-Fi").setView(layout)
            .setPositiveButton("Enregistrer") { _, _ ->
                Prefs.setDeviceIp(this, input.text.toString().trim())
                Prefs.setDeviceMac(this, macInput.text.toString().trim())
                Toast.makeText(this, "Enregistre", Toast.LENGTH_SHORT).show()
                updateDeviceLabel()
            }
            .setNeutralButton("Reveiller (WOL)") { _, _ ->
                val mac = Prefs.getDeviceMac(this)
                if (mac.isNotEmpty()) { WakeOnLan.send(mac); Toast.makeText(this,"Signal envoye", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Fermer", null).show()
    }
}
