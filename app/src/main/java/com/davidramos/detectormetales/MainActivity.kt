package com.davidramos.detectormetales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import android.widget.TextView
import android.widget.Button
import android.widget.ProgressBar
import android.media.AudioManager
import android.media.ToneGenerator

import kotlin.math.sqrt
import kotlin.math.max

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var magnetometer: Sensor? = null

    private lateinit var textX: TextView
    private lateinit var textY: TextView
    private lateinit var textZ: TextView
    private lateinit var textDiff: TextView
    private lateinit var progBar: ProgressBar

    private lateinit var waveView: WavePulseView

    private val bufferSize = 10
    private val bufferX = ArrayList<Float>(bufferSize)
    private val bufferY = ArrayList<Float>(bufferSize)
    private val bufferZ = ArrayList<Float>(bufferSize)
    private val bufferMag = ArrayList<Float>(bufferSize)

    private var minimo = 0f
    private val dynamicThresholdMultiplier = 3f
    private val absoluteMinThreshold = 1.5f

    private var toneGenerator: ToneGenerator? = null
    private var lastBeepTime = 0L

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(ThemePreferences.loadTheme(this))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // -----------------------------
        // 🔹 INICIALIZAR UI DEL DETECTOR
        // -----------------------------
        textX = findViewById(R.id.txtX)
        textY = findViewById(R.id.txtY)
        textZ = findViewById(R.id.txtZ)
        textDiff = findViewById(R.id.txtDiff)
        progBar = findViewById(R.id.progBar)
        waveView = findViewById(R.id.waveView)

        findViewById<Button>(R.id.btnMin).setOnClickListener {
            if (bufferMag.isNotEmpty()) minimo = bufferMag.last()
        }

        // -----------------------------
        // 🔹 SENSORES
        // -----------------------------
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        // -----------------------------
        // 🔹 DRAWER + TOOLBAR
        // -----------------------------
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)


        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // -----------------------------
        // 🔹 EVENTOS DEL MENÚ LATERAL
        // -----------------------------
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_inicio -> {
                    drawerLayout.closeDrawers()
                }

                R.id.nav_calibracion -> {
                    abrirCalibracion()
                }

                R.id.nav_sensibilidad -> {
                    abrirSensibilidad()
                }

                R.id.nav_manual -> {
                    abrirManual()
                }

                R.id.nav_tema -> {
                    abrirSelectorTema()
                }

                R.id.nav_acerca -> {
                    abrirAcercaDe()
                }
            }
            true
        }
    }

    private fun abrirCalibracion() { }
    private fun abrirSensibilidad() { }

    private fun abrirManual() {
        startActivity(Intent(this, ManualActivity::class.java))
        drawerLayout.closeDrawers()
    }
    private fun abrirSelectorTema() {
        val opciones = arrayOf("Claro", "Oscuro")
        val modos = arrayOf(
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
        )

        AlertDialog.Builder(this)
            .setTitle("Selecciona un tema")
            .setItems(opciones) { _, which ->
                val modo = modos[which]
                ThemePreferences.saveTheme(this, modo)
                ThemePreferences.applyTheme(modo)

                recreate()
            }
            .show()
    }
    private fun abrirAcercaDe() {
        startActivity(Intent(this, AcercaDeActivity::class.java))
        drawerLayout.closeDrawers()
    }



    override fun onResume() {
        super.onResume()
        toneGenerator?.release()
        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        toneGenerator?.release()
        toneGenerator = null
    }

    private fun addToBuffer(buffer: ArrayList<Float>, value: Float): Float {
        if (buffer.size >= bufferSize) buffer.removeAt(0)
        buffer.add(value)
        return buffer.average().toFloat()
    }

    private fun stddev(buffer: ArrayList<Float>, mean: Float): Float {
        if (buffer.isEmpty()) return 0f
        var s = 0.0
        for (v in buffer) s += (v - mean) * (v - mean)
        return kotlin.math.sqrt(s / buffer.size).toFloat()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_MAGNETIC_FIELD) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val avgX = addToBuffer(bufferX, x)
        val avgY = addToBuffer(bufferY, y)
        val avgZ = addToBuffer(bufferZ, z)

        val mag = sqrt(x * x + y * y + z * z)
        val avgMag = addToBuffer(bufferMag, mag)

        if (minimo == 0f && bufferMag.size >= bufferSize) minimo = avgMag

        val sdev = stddev(bufferMag, avgMag)
        val umbralDinamico = max(absoluteMinThreshold, sdev * dynamicThresholdMultiplier)

        val diferencia = avgMag - minimo

        textX.text = "X: %.2f".format(avgX)
        textY.text = "Y: %.2f".format(avgY)
        textZ.text = "Z: %.2f".format(avgZ)
        textDiff.text = "Δ: %.2f (umbral %.2f)".format(diferencia, umbralDinamico)

        val intensidad = (diferencia.coerceAtLeast(0f) * 20).toInt().coerceIn(0, 1000)
        val normalized = (intensidad / 80f).coerceIn(0f, 1f)

        progBar.progress = intensidad

        waveView.updateStrength(normalized)

        // Nueva lógica de pitido rápido
        if (diferencia > umbralDinamico) {

            // cuanto más diferencia, más rápido pita:
            val intervalo = (500 - diferencia * 5).toLong().coerceIn(50, 1000)

            val now = System.currentTimeMillis()
            if (now - lastBeepTime > intervalo) {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 80)
                lastBeepTime = now
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
