package com.example.clubdeportivomb

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityRegistroClienteSocioBinding
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper
import com.example.clubdeportivomb.repository.ClubDeportivoRepository
import com.example.clubdeportivomb.utils.AppUtils
import com.example.clubdeportivomb.utils.FileUtils // ✅ IMPORTAR FileUtils
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class RegistroClienteSocio : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteSocioBinding
    private lateinit var repository: ClubDeportivoRepository
    private var certificadoUri: Uri? = null
    private var certificadoSubido = false

    // Contract para seleccionar archivos (PDF o JPG)
    private val selectFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (esFormatoValido(it)) {
                certificadoUri = it
                certificadoSubido = true
                binding.btnAptoFisico.text = "✓ Certificado Cargado"
                binding.btnAptoFisico.setBackgroundColor(getColor(android.R.color.holo_green_light))
                Toast.makeText(this, "Certificado médico cargado exitosamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Formato no válido. Use PDF o JPG", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteSocioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ CORREGIDO: Crear instancia del DBHelper primero
        val dbHelper = ClubDeportivoDBHelper(this)
        repository = ClubDeportivoRepository(dbHelper)

        // === Obtener datos del usuario ===
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"

        // === Mostrar datos del usuario en el header ===
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // === ANIMACIÓN DE LA PELOTA ===
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // === TÍTULO CON "SOCIO" DESTACADO
        AppUtils.setStyledTextWithHighlight(
            binding.titleTipoClienteAgregar,
            "Completa todos los campos de SOCIO que quieres registrar",
            "SOCIO",
            this
        )

        // === Botón volver atrás ===
        binding.iconBack.setOnClickListener {
            finish()
        }

        // === DatePickers ===
        val calendar = Calendar.getInstance()

        binding.etDni.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val dni = s.toString().trim()
                if (dni.length >= 7) {
                    if (repository.existeSocioConDNI(dni)) {
                        // El DNI ya existe: muestra error Y DESACTIVA EL BOTÓN
                        binding.etDni.error = "Este DNI ya está registrado"
                        binding.btnGuardar.isEnabled = false // <--- LÍNEA AÑADIDA
                    } else {
                        // El DNI está disponible: quita el error Y ACTIVA EL BOTÓN
                        binding.etDni.error = null
                        binding.btnGuardar.isEnabled = true // <--- LÍNEA AÑADIDA
                    }
                } else {
                    // Si el DNI es muy corto, no muestres error y asegúrate de que el botón esté activo
                    binding.etDni.error = null
                    binding.btnGuardar.isEnabled = true // <--- LÍNEA AÑADIDA
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

        // === Botón Apto Físico - Cargar certificado médico ===
        binding.btnAptoFisico.setOnClickListener {
            seleccionarCertificado()
        }

        // === Lógica de guardado de socio ===
        binding.btnGuardar.setOnClickListener {
            guardarSocio()
        }

        // === FOOTER AYUDA ===
        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun seleccionarCertificado() {
        AlertDialog.Builder(this)
            .setTitle("Seleccionar certificado médico")
            .setMessage("El certificado médico es OBLIGATORIO para registrar un socio\n\nFormatos aceptados: PDF o JPG")
            .setPositiveButton("📄 PDF") { _, _ ->
                selectFile.launch("application/pdf")
            }
            .setNeutralButton("🖼️ JPG") { _, _ ->
                selectFile.launch("image/jpeg")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun esFormatoValido(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType == "application/pdf" ||
                mimeType == "image/jpeg" ||
                mimeType == "image/jpg"
    }

    private fun obtenerExtensionArchivo(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "application/pdf" -> "pdf"
            "image/jpeg", "image/jpg" -> "jpg"
            else -> "bin"
        }
    }

    private fun guardarSocio() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val fechaAlta = binding.etFechaInscripcion.text.toString().trim()

        // === Validación básica ===
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() ||
            fechaNacimiento.isEmpty() || fechaAlta.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ VALIDACIÓN CRÍTICA: Certificado médico es REQUERIDO para socio
        if (!certificadoSubido || certificadoUri == null) {
            Toast.makeText(
                this,
                "❌ DEBE CARGAR EL CERTIFICADO MÉDICO (PDF o JPG) PARA REGISTRAR UN SOCIO",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Validar que no exista otro socio con el mismo DNI
        if (repository.existeSocioConDNI(dni)) {
            Toast.makeText(this, "Ya existe un socio registrado con este DNI", Toast.LENGTH_LONG).show()
            return
        }

        // Generar nombre único para el certificado CON EXTENSIÓN
        val extension = obtenerExtensionArchivo(certificadoUri!!)
        val nombreCertificado = "certificado_${dni}_${System.currentTimeMillis()}.$extension"

        // Insertar socio completo CON CERTIFICADO
        val socioId = repository.insertarSocioCompleto(
            nombre = nombre,
            apellido = apellido,
            dni = dni,
            fechaNacimiento = fechaNacimiento,
            telefono = telefono,
            direccion = direccion,
            email = email,
            fechaAlta = fechaAlta,
            certificado = nombreCertificado // ✅ OBLIGATORIO
        )

        if (socioId == -1L) {
            Toast.makeText(this, "Error al registrar el socio", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ GUARDAR EL ARCHIVO FÍSICAMENTE usando FileUtils
        val archivoGuardado = guardarArchivoCertificado(certificadoUri!!, nombreCertificado)

        if (archivoGuardado) {
            Toast.makeText(this, "✅ Socio registrado correctamente con certificado médico", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "⚠️ Socio registrado pero hubo un error al guardar el certificado", Toast.LENGTH_LONG).show()
            // Podrías decidir si quieres eliminar el socio o dejarlo sin certificado
        }
    }

    private fun guardarArchivoCertificado(uri: Uri, nombreArchivo: String): Boolean {
        return try {
            // ✅ GUARDAR EL ARCHIVO FÍSICAMENTE usando FileUtils
            val exito = FileUtils.guardarCertificado(this, uri, nombreArchivo)

            if (exito) {
                Toast.makeText(this, "✅ Certificado guardado correctamente", Toast.LENGTH_SHORT).show()
                android.util.Log.d("RegistroSocio", "Certificado físico guardado: $nombreArchivo")
            } else {
                Toast.makeText(this, "❌ Error al guardar el certificado", Toast.LENGTH_SHORT).show()
            }

            exito
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar el certificado", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            false
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