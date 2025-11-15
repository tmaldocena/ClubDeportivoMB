package com.example.clubdeportivomb

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityRegistrarPagoBinding
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper
import com.example.clubdeportivomb.utils.AppUtils
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class RegistrarPagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarPagoBinding
    private lateinit var db: ClubDeportivoDBHelper
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = ClubDeportivoDBHelper(this)

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
//        binding.etBuscarDni.setOnClickListener {
//            buscarPersonaPorDNI()
//
//        }

        binding.etBuscarDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesitamos hacer nada aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Se llama cada vez que el texto cambia
                val dni = s.toString().trim()
                if (dni.length >= 7) { // Opcional: busca solo cuando el DNI tiene una longitud razonable
                    buscarPersonaPorDNI(dni)
                } else {
                    // Si el DNI es muy corto, limpia el campo de nombre
                    binding.etNombreApellido.setText("")
                }
            }

            override fun afterTextChanged(s: Editable) {
                // No necesitamos hacer nada aquí
            }
        })
        

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

        // Horarios
        val horas = arrayOf("18:00", "19:00" , "20:00",  "21:00" )
        val horaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, horas)
        binding.autoHorario.setAdapter(horaAdapter)


        // Tipos de cuota
        val tiposCuota = arrayOf("Diaria", "Mensual", "Promocional")
        val tipoCuotaAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposCuota)
        binding.autoTipoCuota.setAdapter(tipoCuotaAdapter)

        // Medios de pago
        val mediosPago = arrayOf("Efectivo", "Transferencia", "Tarjeta Débito", "Tarjeta Crédito")
        val medioPagoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mediosPago)
        binding.autoMedioPago.setAdapter(medioPagoAdapter)
    }

    private fun buscarPersonaPorDNI(dni: String) {

        if (dni.isEmpty()) {
            Toast.makeText(this, "Ingrese un DNI para buscar", Toast.LENGTH_SHORT).show()
            return
        }

        val persona = db.buscarPersonaPorDNI(dni)

        if (persona != null) {
            val nombreCompleto = "${persona.nombre} ${persona.apellido}"
            binding.etNombreApellido.setText(nombreCompleto)

            Toast.makeText(this, "Cliente encontrado: $nombreCompleto", Toast.LENGTH_SHORT).show()
        } else {
            binding.etNombreApellido.setText("")
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
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

        if (!validarFormulario()) return

        val dni = binding.etBuscarDni.text.toString()
        val actividad = binding.autoActividad.text.toString()
        val horario = binding.autoHorario.text.toString()
        val tipoCuota = binding.autoTipoCuota.text.toString()
        val medioPago = binding.autoMedioPago.text.toString()
        val importe = binding.etImporte.text.toString().toDouble()
        val fecha = binding.etFechaPago.text.toString()

        val id = db.registrarPago(
            dni = dni,
            actividad = actividad,
            horario = horario,
            tipoCuota = tipoCuota,
            medioPago = medioPago,
            importe = importe,
            fechaPago = fecha
        )

        if (id > 0) {
            Toast.makeText(this, "Pago registrado (Recibo Nº $id)", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error al registrar pago", Toast.LENGTH_SHORT).show()
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
