package com.example.clubdeportivomb

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityRegistrarPagoBinding
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarPagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarPagoBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos del usuario
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // Mostrar nombre y rol en el header
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // Configurar dropdowns
        setupDropdowns()

        // Listeners
        binding.etBuscarDni.setOnClickListener {
            buscarSocioPorDNI()
        }

        binding.etFechaPago.setOnClickListener {
            showDatePicker()
        }

        binding.btnRegistrarPago.setOnClickListener {
            registrarPago()
        }

        binding.btnGenerarRecibo.setOnClickListener {
            generarRecibo()
        }

        binding.iconBack.setOnClickListener {
            finish()
        }

        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun setupDropdowns() {
        // Actividades
        val actividades = arrayOf("Fútbol", "Natación", "Tenis", "Básquet", "Vóley")
        val actividadAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, actividades)
        binding.autoActividad.setAdapter(actividadAdapter)

        // Meses de cuota
        val meses = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
        val mesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, meses)
        binding.autoMesCuota.setAdapter(mesAdapter)

        // Años
        val anios = arrayOf("2024", "2025", "2026", "2027", "2028")
        val anioAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, anios)
        binding.autoAnio.setAdapter(anioAdapter)

        // Tipos de cuota
        val tiposCuota = arrayOf("Mensual", "Trimestral", "Anual", "Promocional")
        val tipoCuotaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposCuota)
        binding.autoTipoCuota.setAdapter(tipoCuotaAdapter)

        // Medios de pago
        val mediosPago = arrayOf("Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito")
        val medioPagoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mediosPago)
        binding.autoMedioPago.setAdapter(medioPagoAdapter)
    }

    private fun buscarSocioPorDNI() {
        val dni = binding.etBuscarDni.text.toString().trim()
        if (dni.isEmpty()) {
            Toast.makeText(this, "Ingrese un DNI para buscar", Toast.LENGTH_SHORT).show()
            return
        }

        // Simular búsqueda de socio (aquí iría tu lógica real)
        when (dni) {
            "12345678" -> {
                binding.etNombreApellido.setText("Juan Pérez")
                Toast.makeText(this, "Socio encontrado: Juan Pérez", Toast.LENGTH_SHORT).show()
            }
            "87654321" -> {
                binding.etNombreApellido.setText("María García")
                Toast.makeText(this, "Socio encontrado: María García", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding.etNombreApellido.setText("")
                Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabel()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDateLabel() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etFechaPago.setText(dateFormat.format(calendar.time))
    }

    private fun registrarPago() {
        if (validarFormulario()) {
            Toast.makeText(this, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show()
            // Aquí iría la lógica para guardar en base de datos
        }
    }

    private fun generarRecibo() {
        if (validarFormulario()) {
            Toast.makeText(this, "Recibo generado exitosamente", Toast.LENGTH_SHORT).show()
            // Aquí iría la lógica para generar el recibo/PDF
        }
    }

    private fun validarFormulario(): Boolean {
        if (binding.etBuscarDni.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese el DNI del socio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etNombreApellido.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Busque un socio válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.autoActividad.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Seleccione una actividad", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etImporte.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese el importe", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
}