package com.example.clubdeportivomb

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton

class PagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        // Obtener datos del usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // Configurar header con findViewById
        val tvUsuario = findViewById<TextView>(R.id.tvUsuario)
        tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA
        val imgPelota = findViewById<ImageView>(R.id.imgPelota)
        AppUtils.startBallAnimation(imgPelota, this)

        // Listeners de botones
        findViewById<MaterialButton>(R.id.btnRegistrarPago).setOnClickListener {
            //Toast.makeText(this, "Registrar Pago", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegistrarPagoActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnConsultarEstado).setOnClickListener {
            //Toast.makeText(this, "Consultar Estado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConsultarEstadoActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnHistorialPagos).setOnClickListener {
            //Toast.makeText(this, "Historial de Pagos", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HistorialPagosActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnReportes).setOnClickListener {
            //Toast.makeText(this, "Reportes", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VencimientosActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnConfiguracion).setOnClickListener {
            //Toast.makeText(this, "Configuración de Cuotas", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ConfiguracionCuotasActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.btnVolverMenu).setOnClickListener {
            finish()
        }

        // FOOTER AYUDA
        findViewById<TextView>(R.id.tvAyuda).setOnClickListener {
            showHelpDialog()
        }

        findViewById<ImageView>(R.id.iconBack).setOnClickListener {
            finish()
        }
    }

    // FUNCIÓN PARA MOSTRAR EL MODAL PERSONALIZADO DE AYUDA
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
}