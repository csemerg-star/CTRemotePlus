package com.ctremoteplus.app

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ctremoteplus.app.databinding.ActivityDevicesBinding

class DeviceSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDevicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val devices = listOf(
            "TV" to android.R.drawable.ic_menu_view,
            "Décodeur" to android.R.drawable.ic_menu_sort_by_size,
            "Lecteur DVD" to android.R.drawable.ic_media_play,
            "Système Audio" to android.R.drawable.ic_lock_silent_mode_off,
            "Projecteur" to android.R.drawable.ic_menu_camera,
            "Climatiseur" to android.R.drawable.ic_menu_compass,
            "Ventilateur" to android.R.drawable.ic_menu_rotate
        )

        for ((name, iconRes) in devices) {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0; height = 220
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(10, 10, 10, 10)
                }
            }
            val img = ImageView(this).apply {
                setImageResource(iconRes)
                layoutParams = LinearLayout.LayoutParams(80, 80)
            }
            val txt = TextView(this).apply {
                text = name
                textSize = 13f
                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.text_dark))
                setPadding(0, 16, 0, 0)
            }
            layout.addView(img)
            layout.addView(txt)
            layout.setOnClickListener {
                Prefs.setDeviceType(this, name)
                startActivity(Intent(this, MainActivity::class.java))
            }
            binding.deviceGrid.addView(layout)
        }
    }
}
