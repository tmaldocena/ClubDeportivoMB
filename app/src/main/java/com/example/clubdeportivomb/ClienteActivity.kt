package com.example.clubdeportivomb

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityClienteBinding
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton

class ClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // Mostrar nombre y rol en el header USANDO BINDING
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA USANDO BINDING
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // Listeners de botones
        binding.btnAgregarNuevoCliente.setOnClickListener {
            //Toast.makeText(this, "Nuevo Cliente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TipoClienteAgregarClienteActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        // BOTÓN DE PAGOS (NUEVO BOTÓN)
        binding.btnPagos.setOnClickListener {
            //Toast.makeText(this, "Pagos", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PagosActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        binding.btnBuscarCliente.setOnClickListener {
            //Toast.makeText(this, "Buscar Cliente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ClienteBuscarCliente::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        binding.btnControlAcceso.setOnClickListener {
            //Toast.makeText(this, "Control de Acceso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ControlAccesoActivity::class.java)
            intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
            intent.putExtra("ROL_USUARIO", rolUsuario)
            startActivity(intent)
        }

        binding.btnVolverMenu.setOnClickListener {
            finish()
        }

        // === FOOTER AYUDA === (ACTUALIZA ESTAS LÍNEAS)
        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }

        binding.iconBack.setOnClickListener {
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