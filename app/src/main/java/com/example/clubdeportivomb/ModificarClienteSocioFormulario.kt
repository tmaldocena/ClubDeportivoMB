package com.example.clubdeportivomb

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityModificarClienteSocioFormularioBinding
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper
import com.example.clubdeportivomb.model.Socio
import com.example.clubdeportivomb.repository.ClubDeportivoRepository
import com.example.clubdeportivomb.utils.AppUtils
import com.example.clubdeportivomb.utils.FileUtils
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class ModificarClienteSocioFormulario : AppCompatActivity() {

    private lateinit var binding: ActivityModificarClienteSocioFormularioBinding
    private lateinit var repository: ClubDeportivoRepository
    private var socio: Socio? = null
    private var certificadoUri: Uri? = null
    private var certificadoActualizado = false

    // Contract para seleccionar archivos (PDF o JPG)
    private val selectFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (esFormatoValido(it)) {
                certificadoUri = it
                certificadoActualizado = true
                binding.btnActualizarCertificado.text = "✓ Certificado Actualizado"
                binding.btnActualizarCertificado.setBackgroundColor(getColor(android.R.color.holo_green_light))
                // Ocultar botón de ver certificado anterior si existe
                binding.btnVerCertificadoActual.visibility = View.GONE
                Toast.makeText(this, "Certificado médico actualizado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Formato no válido. Use PDF o JPG", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModificarClienteSocioFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Repository
        val dbHelper = ClubDeportivoDBHelper(this)
        repository = ClubDeportivoRepository(dbHelper)

        // Obtener datos del usuario y ID del socio
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"
        val socioId = intent.getLongExtra("SOCIO_ID", -1L)

        // Mostrar datos del usuario en el header
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // ANIMACIÓN DE LA PELOTA
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // TÍTULO CON "SOCIO" DESTACADO
        AppUtils.setStyledTextWithHighlight(
            binding.titleTipoClienteModificar,
            "Modifica los datos del SOCIO",
            "SOCIO",
            this
        )

        // Botón volver atrás
        binding.iconBack.setOnClickListener {
            finish()
        }

        // DatePickers
        val calendar = Calendar.getInstance()

        binding.etFechaNacimiento.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    binding.etFechaNacimiento.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etFechaInscripcion.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    binding.etFechaInscripcion.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Botón Actualizar Certificado
        binding.btnActualizarCertificado.setOnClickListener {
            seleccionarCertificado()
        }

        // Botón Ver Certificado Actual
        binding.btnVerCertificadoActual.setOnClickListener {
            verCertificadoActual()
        }

        // Botón Guardar
        binding.btnGuardar.setOnClickListener {
            mostrarConfirmacionGuardar()
        }

        // FOOTER AYUDA
        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }

        // Cargar datos del socio
        if (socioId != -1L) {
            cargarDatosSocio(socioId)
        } else {
            Toast.makeText(this, "Error: No se proporcionó ID de socio", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarDatosSocio(socioId: Long) {
        socio = repository.obtenerSocioPorId(socioId)
        socio?.let {
            // Llenar campos con datos del socio
            binding.etNombre.setText(it.nombre)
            binding.etApellido.setText(it.apellido)
            binding.etDni.setText(it.dni)
            binding.etDni.isEnabled = false // DNI no se puede modificar
            binding.etFechaNacimiento.setText(it.fechaNacimiento)
            binding.etTelefono.setText(it.telefono)
            binding.etDireccion.setText(it.direccion)
            binding.etEmail.setText(it.email)
            binding.etFechaInscripcion.setText(it.fechaAlta)
            binding.switchEstado.isChecked = it.estado == 1

            // Mostrar estado del certificado actual
            if (it.certificado?.isNotEmpty() == true) {
                binding.btnActualizarCertificado.text = "✓ Certificado Actual"
                binding.btnVerCertificadoActual.visibility = View.VISIBLE
                binding.tvInfoCertificado.text = "Certificado actual: ${it.certificado}"
            } else {
                binding.btnVerCertificadoActual.visibility = View.GONE
                binding.tvInfoCertificado.text = "Sin certificado cargado"
            }
        } ?: run {
            Toast.makeText(this, "Error: Socio no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun seleccionarCertificado() {
        AlertDialog.Builder(this)
            .setTitle("Actualizar certificado médico")
            .setMessage("Seleccione el nuevo certificado médico\n\nFormatos aceptados: PDF o JPG")
            .setPositiveButton("📄 PDF") { _, _ ->
                selectFile.launch("application/pdf")
            }
            .setNeutralButton("🖼️ JPG") { _, _ ->
                selectFile.launch("image/jpeg")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verCertificadoActual() {
        socio?.let { socioActual ->
            if (socioActual.certificado?.isNotEmpty() == true) {
                val certificadoUri = FileUtils.obtenerCertificado(this, socioActual.certificado)
                if (certificadoUri != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(certificadoUri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No hay aplicación para abrir PDF", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Certificado no encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No hay certificado cargado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun esFormatoValido(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType == "application/pdf" || mimeType == "image/jpeg" || mimeType == "image/jpg"
    }

    private fun mostrarConfirmacionGuardar() {
        val nombreCompleto = "${binding.etNombre.text} ${binding.etApellido.text}"

        AlertDialog.Builder(this)
            .setTitle("Confirmar Actualización")
            .setMessage("¿Está seguro que desea actualizar los datos de $nombreCompleto?")
            .setPositiveButton("Sí, Actualizar") { _, _ ->
                actualizarSocio()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarSocio() {
        try {
            socio?.let { socioActual ->
                val nombre = binding.etNombre.text.toString().trim()
                val apellido = binding.etApellido.text.toString().trim()
                val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()
                val telefono = binding.etTelefono.text.toString().trim()
                val direccion = binding.etDireccion.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val fechaAlta = binding.etFechaInscripcion.text.toString().trim()
                val estado = if (binding.switchEstado.isChecked) 1 else 0

                // Validación básica
                if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento.isEmpty() || fechaAlta.isEmpty()) {
                    Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return
                }

                // Validar formato de fechas
                if (!validarFormatoFecha(fechaNacimiento) || !validarFormatoFecha(fechaAlta)) {
                    Toast.makeText(this, "Formato de fecha inválido. Use DD/MM/AAAA", Toast.LENGTH_SHORT).show()
                    return
                }

                // Validar email si está presente
                if (email.isNotEmpty() && !validarEmail(email)) {
                    Toast.makeText(this, "Formato de email inválido", Toast.LENGTH_SHORT).show()
                    return
                }

                // Manejar el certificado
                var nombreCertificadoActualizado: String? = null
                var certificadoAnterior: String? = null

                if (certificadoActualizado && certificadoUri != null) {
                    val extension = obtenerExtensionArchivo(certificadoUri!!)
                    nombreCertificadoActualizado = "certificado_${socioActual.dni}_${System.currentTimeMillis()}.$extension"
                    val exitoGuardado = guardarArchivoCertificado(certificadoUri!!, nombreCertificadoActualizado)
                    if (!exitoGuardado) {
                        Toast.makeText(this, "Error al guardar el certificado", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // Guardar referencia al certificado anterior para eliminarlo después
                    certificadoAnterior = socioActual.certificado
                }

                // ACTUALIZAR EN LA BASE DE DATOS
                val exito = repository.actualizarSocio(
                    socioId = socioActual.id,
                    nombre = nombre,
                    apellido = apellido,
                    fechaNacimiento = fechaNacimiento,
                    telefono = telefono,
                    direccion = direccion,
                    email = email,
                    fechaAlta = fechaAlta,
                    estado = estado,
                    certificado = nombreCertificadoActualizado ?: socioActual.certificado
                )

                if (exito) {
                    Toast.makeText(this, "✅ Socio actualizado correctamente", Toast.LENGTH_LONG).show()

                    // Eliminar certificado anterior si se actualizó
                    if (certificadoAnterior?.isNotEmpty() == true) {
                        val eliminado = FileUtils.eliminarCertificado(this, certificadoAnterior)
                        if (!eliminado) {
                            Log.w("ModificarSocio", "No se pudo eliminar el certificado anterior: $certificadoAnterior")
                        }
                    }

                    setResult(RESULT_OK) // Para notificar a la actividad anterior
                    finish()
                } else {
                    Toast.makeText(this, "❌ Error al actualizar el socio", Toast.LENGTH_SHORT).show()

                    // Si falló la actualización, eliminar el certificado nuevo que se guardó
                    if (nombreCertificadoActualizado != null) {
                        FileUtils.eliminarCertificado(this, nombreCertificadoActualizado)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun obtenerExtensionArchivo(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "application/pdf" -> "pdf"
            "image/jpeg", "image/jpg" -> "jpg"
            else -> "bin"
        }
    }

    private fun guardarArchivoCertificado(uri: Uri, nombreArchivo: String): Boolean {
        return try {
            val exito = FileUtils.guardarCertificado(this, uri, nombreArchivo)
            if (exito) {
                Toast.makeText(this, "✅ Certificado actualizado", Toast.LENGTH_SHORT).show()
            }
            exito
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar el certificado", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun validarFormatoFecha(fecha: String): Boolean {
        if (fecha.isEmpty()) return false

        try {
            // Intentar parsear la fecha en diferentes formatos
            val formatos = listOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "dd.MM.yyyy")

            for (formato in formatos) {
                try {
                    val sdf = java.text.SimpleDateFormat(formato, java.util.Locale.getDefault())
                    sdf.isLenient = false // No permitir fechas inválidas como 31/02/2023
                    val date = sdf.parse(fecha)
                    if (date != null) {
                        return true
                    }
                } catch (e: Exception) {
                    // Continuar con el siguiente formato
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    private fun validarEmail(email: String): Boolean {
        if (email.isEmpty()) return true // Email es opcional
        val pattern = Regex("""^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
        return pattern.matches(email)
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
        btnVolver.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}