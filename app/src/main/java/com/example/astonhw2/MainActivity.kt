package com.example.astonhw2

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var rainbowDrum: RainbowWheelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rainbowDrum = findViewById<RainbowWheelView>(R.id.rainbow_wheel_view)
        val dltBtn = findViewById<Button>(R.id.btn_delete)

        rainbowDrum.setOnClickListener{
            rainbowDrum.spinWheel()
        }

        dltBtn.setOnClickListener{
            rainbowDrum.clearCustomView()
        }

        val seekBar = findViewById<SeekBar>(R.id.seek_bar)

        seekBar.max = 100
        seekBar.progress = 50

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newWidth = calculateNewWidth(progress)
                updateRainbowWheelSize(newWidth)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun calculateNewWidth(progress: Int): Int {
        val maxWidth = resources.displayMetrics.widthPixels
        val minRainbowWidth = 200
        val newWidth = (minRainbowWidth + (maxWidth - minRainbowWidth) * progress / 100)
        return newWidth
    }

    private fun updateRainbowWheelSize(newWidth: Int) {
        val params = rainbowDrum.layoutParams
        params.width = newWidth
        params.height = newWidth

        rainbowDrum.layoutParams = params
    }
}