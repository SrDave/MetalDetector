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

    // Código eliminado por razones de licencia

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(ThemePreferences.loadTheme(this))
        super.onCreate(savedInstanceState)
        
        // Código eliminado por razones de licencia
    }
}
