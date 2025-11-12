package com.example.clubdeportivomb

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import com.example.clubdeportivomb.db.ClubDeportivoDBHelper

class VencimientosActivity : AppCompatActivity() {

    data class Cliente(
        val nombre: String,
        val esSocio: Boolean,
        val fechaVencimiento: String,
        val fechaRaw: String
    )

    private lateinit var tablaVencimientos: TableLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vencimientos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val iconBack = findViewById<ImageView>(R.id.iconBack)
        tablaVencimientos = findViewById(R.id.tablaVencimientos)
        val searchView = findViewById<SearchView>(R.id.searchView)
        iconBack.setOnClickListener { finish() }

        val dbHelper = ClubDeportivoDBHelper(this)
        val listaSocios = dbHelper.obtenerSociosConVencimiento()
        val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val hoy = LocalDate.now()

        val vencidos = mutableListOf<Cliente>()
        val hoyVencen = mutableListOf<Cliente>()

        for (cliente in listaSocios) {
            try {
                val fecha = LocalDate.parse(cliente.fechaVencimiento, formatoFecha)
                when {
                    fecha.isEqual(hoy) -> hoyVencen.add(cliente)
                    fecha.isBefore(hoy) -> vencidos.add(cliente)
                }
            } catch (_: Exception)
            {
                false
            }
        }


        val hace60Dias = hoy.minusDays(60)

        val vencidosUltimos60 = vencidos.filter {
            try {
                val fecha = LocalDate.parse(it.fechaVencimiento, formatoFecha)
                fecha.isAfter(hace60Dias)
            } catch (_: Exception) {
                false
            }
        }

        val listaFinal = hoyVencen + vencidosUltimos60
        actualizarTabla(listaFinal)

        // --- Búsqueda ---
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val texto = newText?.trim()?.lowercase() ?: ""
                val filtrada = listaFinal.filter {
                    it.nombre.lowercase().contains(texto) || it.fechaVencimiento.contains(texto) }
                actualizarTabla(filtrada)
                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarTabla(listaClientes: List<Cliente>) {
        val childCount = tablaVencimientos.childCount
        if (childCount > 1) {
            tablaVencimientos.removeViews(1, childCount - 1)
        }

        val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val hoy = LocalDate.now()

        // Ordenar: más viejas primero
        val listaOrdenada = listaClientes.sortedBy { cliente ->
            try {
                LocalDate.parse(cliente.fechaVencimiento, formatoFecha)
            } catch (e: Exception) {
                LocalDate.MAX
            }
        }

        for (cliente in listaOrdenada) {
            val fila = TableRow(this)
            val tvNombre = crearCelda(cliente.nombre)
            val tvTipo = crearCelda(if (cliente.esSocio) "Socio" else "No Socio")
            val tvFecha = crearCelda(cliente.fechaVencimiento, alinearDerecha = true)

            try {
                val fechaVencimiento = LocalDate.parse(cliente.fechaVencimiento, formatoFecha)
                when {
                    fechaVencimiento.isEqual(hoy) -> {
                        // Rojo si vence hoy
                        fila.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                    }
                    fechaVencimiento.isBefore(hoy) -> {
                        // Azul si ya venció
                        fila.setBackgroundColor(ContextCompat.getColor(this, R.color.fila_vencida_azul))
                    }
                }
            } catch (_: DateTimeParseException) { }

            fila.addView(tvNombre)
            fila.addView(tvTipo)
            fila.addView(tvFecha)
            tablaVencimientos.addView(fila)
        }
    }

    private fun crearCelda(texto: String, alinearDerecha: Boolean = false): TextView {
        val textView = TextView(this)
        textView.text = texto
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        textView.setPadding(8, 8, 8, 8)
        textView.textSize = 14f
        if (alinearDerecha) textView.gravity = Gravity.END
        return textView
    }
}
