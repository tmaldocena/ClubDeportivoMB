package com.example.clubdeportivomb

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityTipoClienteAgregarClienteBinding
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton

class TipoClienteAgregarClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTipoClienteAgregarClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipoClienteAgregarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // Mostrar nombre en el header USANDO BINDING
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA USANDO BINDING
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // Listeners de botones USANDO BINDING
        binding.btnTipoClienteSocio.setOnClickListener {
            //Toast.makeText(this, "Botón Socio presionado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegistroClienteSocio::class.java)
            // PASAR LOS DATOS DEL USUARIO
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        binding.btnTipoClienteNoSocio.setOnClickListener {
            //Toast.makeText(this, "Botón No Socio presionado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegistroClienteNoSocio::class.java)
            // PASAR LOS DATOS DEL USUARIO
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        binding.iconBack.setOnClickListener {
            finish()
        }

        // === FOOTER AYUDA === (AGREGA ESTAS LÍNEAS)
        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
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