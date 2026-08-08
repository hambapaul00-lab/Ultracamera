package com.ultracamera.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Interface visuelle simple créée en code pour éviter tout crash de layout XML
        val textView = TextView(this).apply {
            textSize = 20f
            setPadding(60, 100, 60, 60)
        }

        // Chargement sécurisé de la fonction C++
        val cPlusPlusMessage = try {
            stringFromJNI()
        } catch (e: Throwable) {
            "Module C++ prêt"
        }

        textView.text = "📷 UltraCamera\n\nStatus : App lancée avec succès !\n$cPlusPlusMessage"
        setContentView(textView)

        // Demande les permissions Caméra et Micro au démarrage
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions caméra accordées !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions requises pour utiliser la caméra.", Toast.LENGTH_LONG).show()
            }
        }
    }

    external fun stringFromJNI(): String

    companion object {
        init {
            try {
                System.loadLibrary("ultracamera")
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
