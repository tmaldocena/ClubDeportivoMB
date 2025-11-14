package com.example.clubdeportivomb

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityControlAccesoBinding
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton

class ControlAccesoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControlAccesoBinding
    private var scanAnimator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlAccesoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // Configurar header
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // INICIAR ANIMACIÓN DEL SCANNER
        startScanAnimation()

        // Listeners básicos
        binding.btnEscanearManualmente.setOnClickListener {
            Toast.makeText(this, "Escaneo manual - Funcionalidad pendiente", Toast.LENGTH_SHORT).show()
        }

        // Simular escaneo al tocar el área del scanner (para demo)
        binding.scanLine.setOnClickListener {
            simularEscaneoQR()
        }

        binding.iconBack.setOnClickListener {
            finish()
        }

        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun startScanAnimation() {
        val scanLine = binding.scanLine
        scanAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val translationY = (250 - 8) * progress // 250dp height - 4dp line height
                scanLine.translationY = translationY - 125 // Centrar
            }
        }
        scanAnimator?.start()
    }

    private fun simularEscaneoQR() {
        // Simular escaneo exitoso
        scanAnimator?.cancel()

        Toast.makeText(this, "Código QR escaneado - Funcionalidad pendiente", Toast.LENGTH_SHORT).show()

        // Reiniciar animación después de 2 segundos
        binding.root.postDelayed({
            startScanAnimation()
        }, 2000)
    }

    private fun showHelpDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_modal_ayuda, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.6f)

        val btnVolver = dialogView.findViewById<MaterialButton>(R.id.button)
        btnVolver.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        scanAnimator?.cancel()
    }
}