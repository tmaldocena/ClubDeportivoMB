package com.example.clubdeportivomb

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityRegistrarPagoBinding

class RegistrarPagoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarPagoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Cargar opciones de los menús desplegables ---
        configurarDropdowns()

        // --- Buscar cliente por DNI ---
        binding.etBuscarDni.setOnEditorActionListener { v, actionId, event ->
            val dni = binding.etBuscarDni.text.toString()
            if (dni.isNotEmpty()) {
                buscarClientePorDni(dni)
            } else {
                Toast.makeText(this, "Ingresá un DNI", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // --- Registrar pago ---
        binding.btnRegistrarPago.setOnClickListener {
            registrarPago()
        }

        // --- Botón de volver ---
        binding.iconBack.setOnClickListener {
            finish()
        }
    }


    // MÉTODOS AUXILIARES


    private fun configurarDropdowns() {
        val actividades = listOf("Fútbol", "Natación", "Gimnasio", "Yoga", "Tenis")
        val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        val horarios = listOf("8:00", "10:00", "14:00", "18:00", "20:00")
        val tiposCuota = listOf("Mensual", "Trimestral", "Anual")
        val mediosPago = listOf("Efectivo", "Transferencia", "Tarjeta")

        binding.autoActividad.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, actividades))
        binding.autoDia.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, dias))
        binding.autoHorario.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, horarios))
        binding.autoTipoCuota.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, tiposCuota))
        binding.autoMedioPago.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, mediosPago))
    }

    private fun buscarClientePorDni(dni: String) {

        if (dni == "12345678") {
            binding.etNombreApellido.setText("Juan Pérez")
            Toast.makeText(this, "Cliente encontrado: Socio", Toast.LENGTH_SHORT).show()
        } else {
            binding.etNombreApellido.setText("Cliente no registrado")
            Toast.makeText(this, "No se encontró cliente con ese DNI", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarPago() {
        val nombre = binding.etNombreApellido.text.toString()
        val actividad = binding.autoActividad.text.toString()
        val dia = binding.autoDia.text.toString()
        val horario = binding.autoHorario.text.toString()
        val tipoCuota = binding.autoTipoCuota.text.toString()
        val medioPago = binding.autoMedioPago.text.toString()
        val importe = binding.etImporte.text.toString()

        if (nombre.isEmpty() || actividad.isEmpty() || medioPago.isEmpty() || importe.isEmpty()) {
            Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show()
            return
        }



    }
}
