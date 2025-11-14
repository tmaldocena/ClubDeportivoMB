package com.example.clubdeportivomb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clubdeportivomb.adapter.ClienteAdapter
import com.example.clubdeportivomb.databinding.ActivityClienteBuscarClienteResultadoBinding
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper
import com.example.clubdeportivomb.model.NoSocio
import com.example.clubdeportivomb.model.Socio
import com.example.clubdeportivomb.repository.ClubDeportivoRepository
import com.example.clubdeportivomb.utils.AppUtils
import com.example.clubdeportivomb.utils.FileUtils
import com.google.android.material.button.MaterialButton

class ClienteBuscarClienteResultado : AppCompatActivity() {

    private lateinit var binding: ActivityClienteBuscarClienteResultadoBinding
    private lateinit var repository: ClubDeportivoRepository
    private lateinit var adapter: ClienteAdapter
    private var resultadosClientes: List<Any> = emptyList()
    private var textoBusquedaGlobal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteBuscarClienteResultadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Repository
        val dbHelper = ClubDeportivoDBHelper(this)
        repository = ClubDeportivoRepository(dbHelper)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener datos del usuario y búsqueda
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val rolUsuario = intent.getStringExtra("ROL_USUARIO") ?: "Invitado"
        textoBusquedaGlobal = intent.getStringExtra("TEXTO_BUSQUEDA") ?: ""

        // Mostrar datos del usuario en el header
        binding.tvUsuario.text = "$nombreUsuario - $rolUsuario"

        // Actualizar título con el texto de búsqueda
        binding.titleResultadoBusquedaCliente.text = "Resultados para: $textoBusquedaGlobal"

        // ANIMACIÓN DE LA PELOTA
        AppUtils.startBallAnimation(binding.imgPelota, this)

        // Configurar RecyclerView - INICIALIZAR PRIMERO
        configurarRecyclerView()

        // Botón volver atrás
        binding.iconBack.setOnClickListener {
            finish()
        }

        // Botones de acción
        binding.btnModificar.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                modificarCliente(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEliminar.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                mostrarDialogoEliminar(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Ver (Certificado)
        binding.btnVer.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                verCertificado(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Pagos
        binding.btnPagos.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                verPagos(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Actividades
        binding.btnActividades.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                verActividades(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Asistencias
        binding.btnAsistencias.setOnClickListener {
            val clienteSeleccionado = adapter.getClienteSeleccionado()
            if (clienteSeleccionado != null) {
                verAsistencias(clienteSeleccionado)
            } else {
                Toast.makeText(this, "Seleccione un cliente primero", Toast.LENGTH_SHORT).show()
            }
        }

        // FOOTER AYUDA
        binding.tvAyuda.setOnClickListener {
            showHelpDialog()
        }

        // === BUSCAR EN LA BASE DE DATOS ===
        buscarClientesEnDB(textoBusquedaGlobal)
    }

    private fun configurarRecyclerView() {
        // INICIALIZAR ADAPTER con lista vacía primero
        adapter = ClienteAdapter(emptyList()) { cliente ->
            // Actualizar texto de selección
            val nombreCliente = when (cliente) {
                is Socio -> "${cliente.nombre} ${cliente.apellido}"
                is NoSocio -> "${cliente.nombre} ${cliente.apellido}"
                else -> "Cliente seleccionado"
            }
            binding.tvSeleccionado.text = "Seleccionado: $nombreCliente"
        }

        // Configurar RecyclerView
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter
    }

    // FUNCIÓN PARA BUSCAR EN LA BASE DE DATOS
    private fun buscarClientesEnDB(textoBusqueda: String) {
        try {
            resultadosClientes = repository.buscarTodosLosClientes(textoBusqueda)

            if (resultadosClientes.isEmpty()) {
                binding.titleResultadoBusquedaCliente.text = "No se encontraron resultados para: $textoBusqueda"
                binding.tvSeleccionado.text = "No hay clientes para mostrar"
                Toast.makeText(this, "No se encontraron clientes", Toast.LENGTH_LONG).show()
            } else {
                val cantidadResultados = resultadosClientes.size
                binding.titleResultadoBusquedaCliente.text = "Encontrados $cantidadResultados resultados para: $textoBusqueda"
                binding.tvSeleccionado.text = "Selecciona un cliente:"

                // ✅ ACTUALIZAR EL ADAPTER CORRECTAMENTE
                adapter.actualizarDatos(resultadosClientes)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al buscar clientes", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun modificarCliente(cliente: Any) {
        when (cliente) {
            is Socio -> {
                val intent = Intent(this, ModificarClienteSocioFormulario::class.java).apply {
                    putExtra("SOCIO_ID", cliente.id)
                    putExtra("NOMBRE_USUARIO", intent.getStringExtra("NOMBRE_USUARIO"))
                    putExtra("ROL_USUARIO", intent.getStringExtra("ROL_USUARIO"))
                }
                startActivity(intent)
            }
            is NoSocio -> {
                val intent = Intent(this, ModificarClienteNoSocioFormulario::class.java).apply {
                    putExtra("NO_SOCIO_ID", cliente.id)
                    putExtra("NOMBRE_USUARIO", intent.getStringExtra("NOMBRE_USUARIO"))
                    putExtra("ROL_USUARIO", intent.getStringExtra("ROL_USUARIO"))
                }
                startActivity(intent)
            }
        }
    }

    private fun mostrarDialogoEliminar(cliente: Any) {
        val nombreCliente = when (cliente) {
            is Socio -> "${cliente.nombre} ${cliente.apellido}"
            is NoSocio -> "${cliente.nombre} ${cliente.apellido}"
            else -> "este cliente"
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Está seguro que desea eliminar a $nombreCliente?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarCliente(cliente)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarCliente(cliente: Any) {
        val exito = when (cliente) {
            is Socio -> {
                // Eliminar certificado si existe
                if (cliente.certificado?.isNotEmpty() == true) {
                    FileUtils.eliminarCertificado(this, cliente.certificado)
                }
                repository.eliminarSocio(cliente.id)
            }
            is NoSocio -> {
                // Eliminar certificado si existe
                if (cliente.certificado?.isNotEmpty() == true) {
                    FileUtils.eliminarCertificado(this, cliente.certificado)
                }
                repository.eliminarNoSocio(cliente.id)
            }
            else -> false
        }

        val nombreCliente = when (cliente) {
            is Socio -> "${cliente.nombre} ${cliente.apellido}"
            is NoSocio -> "${cliente.nombre} ${cliente.apellido}"
            else -> "Cliente"
        }

        if (exito) {
            Toast.makeText(this, "✅ $nombreCliente eliminado correctamente", Toast.LENGTH_LONG).show()

            // ✅ RECARGAR LA LISTA CORRECTAMENTE
            buscarClientesEnDB(textoBusquedaGlobal)

            // Limpiar selección
            binding.tvSeleccionado.text = "Selecciona un cliente:"
        } else {
            Toast.makeText(this, "❌ Error al eliminar $nombreCliente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verCertificado(cliente: Any) {
        val nombreCertificado = when (cliente) {
            is Socio -> cliente.certificado
            is NoSocio -> cliente.certificado
            else -> null
        }

        if (nombreCertificado != null && nombreCertificado.isNotEmpty()) {
            val certificadoUri = FileUtils.obtenerCertificado(this, nombreCertificado)
            if (certificadoUri != null) {
                // Abrir el certificado con una app compatible
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
            Toast.makeText(this, "El cliente no tiene certificado cargado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verPagos(cliente: Any) {
        val dni = when (cliente) {
            is Socio -> cliente.dni
            is NoSocio -> cliente.dni
            else -> ""
        }
        Toast.makeText(this, "Mostrando pagos de DNI: $dni", Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a pantalla de pagos
    }

    private fun verActividades(cliente: Any) {
        val nombreCliente = when (cliente) {
            is Socio -> "${cliente.nombre} ${cliente.apellido}"
            is NoSocio -> "${cliente.nombre} ${cliente.apellido}"
            else -> "Cliente"
        }
        Toast.makeText(this, "Mostrando actividades de $nombreCliente", Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a pantalla de actividades
    }

    private fun verAsistencias(cliente: Any) {
        val nombreCliente = when (cliente) {
            is Socio -> "${cliente.nombre} ${cliente.apellido}"
            is NoSocio -> "${cliente.nombre} ${cliente.apellido}"
            else -> "Cliente"
        }
        Toast.makeText(this, "Mostrando asistencias de $nombreCliente", Toast.LENGTH_SHORT).show()
        // TODO: Implementar navegación a pantalla de asistencias
    }

    // Botón físico BACK - vuelve directamente
    override fun onBackPressed() {
        finish()
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