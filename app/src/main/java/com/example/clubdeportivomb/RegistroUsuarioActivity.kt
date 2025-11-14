package com.example.clubdeportivomb

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubdeportivomb.databinding.ActivityRegistroUsuarioBinding
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper
import com.example.clubdeportivomb.repository.ClubDeportivoRepository
import java.util.Calendar
import android.widget.ArrayAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class RegistroUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroUsuarioBinding
    private lateinit var repository: ClubDeportivoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dbHelper = ClubDeportivoDBHelper(this)
        repository = ClubDeportivoRepository(dbHelper)

        // ✅ CONFIGURAR EL SPINNER/COMBOBOX DE ÁREAS
        configurarSpinnerAreas()

        // Configurar DatePickers
        setupDatePickers()

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val dni = binding.etDni.text.toString().trim()
            val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()
            val telefono = binding.etTelefono.text.toString().trim()
            val direccion = binding.etDireccion.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val fechaAlta = binding.etFechaInscripcion.text.toString().trim()
            val username = binding.etUsuario.text.toString().trim()
            val password = binding.etContrasena.text.toString().trim()
            val rol = binding.autoCompleteArea.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || username.isEmpty() || password.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que las contraseñas coincidan
            val confirmarPassword = binding.etConfirmarContrasena.text.toString().trim()
            if (password != confirmarPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que el rol sea uno de los permitidos
            val rolesPermitidos = resources.getStringArray(R.array.areas_array)
            if (!rolesPermitidos.contains(rol)) {
                Toast.makeText(this, "Seleccione un área válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordHash = password.hashCode().toString()

            val usuarioId = repository.insertarUsuarioCompleto(
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                fechaNacimiento = fechaNacimiento,
                telefono = telefono,
                direccion = direccion,
                email = email,
                fechaAlta = fechaAlta,
                username = username,
                passwordHash = passwordHash,
                rol = rol
            )

            if (usuarioId != -1L) {
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Usar tvIniciaSesion
        binding.tvIniciaSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Usar btnCancelar
        binding.btnCancelar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Usar iconBack
        binding.iconBack.setOnClickListener {
            finish()
        }
    }

    private fun configurarSpinnerAreas() {
        // Obtener el array de strings desde resources
        val areasArray = resources.getStringArray(R.array.areas_array)

        // Crear un ArrayAdapter para el AutoCompleteTextView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            areasArray
        )

        // Asignar el adapter al AutoCompleteTextView
        binding.autoCompleteArea.setAdapter(adapter)

        // Opcional: Configurar el número de caracteres para mostrar sugerencias
        binding.autoCompleteArea.threshold = 1

        // Opcional: Establecer un hint
        binding.autoCompleteArea.hint = "Seleccione un área"
    }

    private fun setupDatePickers() {
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



        val autoCompleteArea = findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteArea)

        val opciones = listOf("Administración", "Profesores", "Nutricionistas")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            opciones
        )

        autoCompleteArea.setAdapter(adapter)

    }
}