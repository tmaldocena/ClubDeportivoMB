package com.example.clubdeportivomb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clubdeportivomb.model.NoSocio
import com.example.clubdeportivomb.model.Socio
import com.google.android.material.card.MaterialCardView
import com.example.clubdeportivomb.R

class ClienteAdapter(
    private var clientes: List<Any>,
    private val onClienteClick: (Any) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    private var clienteSeleccionado: Any? = null

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardCliente: MaterialCardView = itemView.findViewById(R.id.cardCliente)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDni: TextView = itemView.findViewById(R.id.tvDni)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val tvCertificado: TextView = itemView.findViewById(R.id.tvCertificado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = clientes[position]

        when (cliente) {
            is Socio -> {
                holder.tvNombre.text = "${cliente.nombre} ${cliente.apellido}"
                holder.tvDni.text = "DNI: ${cliente.dni}"
                holder.tvTipo.text = "SOCIO"
                holder.tvTipo.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
                holder.tvCertificado.text = if (cliente.certificado?.isNotEmpty() == true) "✓ Certificado" else "✗ Sin certificado"
            }
            is NoSocio -> {
                holder.tvNombre.text = "${cliente.nombre} ${cliente.apellido}"
                holder.tvDni.text = "DNI: ${cliente.dni}"
                holder.tvTipo.text = "NO SOCIO"
                holder.tvTipo.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_dark))
                holder.tvCertificado.text = if (cliente.certificado?.isNotEmpty() == true) "✓ Certificado" else "✗ Sin certificado"
            }
        }

        // Resaltar selección
        val isSelected = cliente == clienteSeleccionado
        holder.cardCliente.strokeColor = if (isSelected) {
            ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light)
        } else {
            ContextCompat.getColor(holder.itemView.context, android.R.color.transparent)
        }
        holder.cardCliente.strokeWidth = if (isSelected) 4 else 0

        holder.cardCliente.setOnClickListener {
            clienteSeleccionado = cliente
            notifyDataSetChanged()
            onClienteClick(cliente)
        }
    }

    override fun getItemCount(): Int = clientes.size

    // ✅ FUNCIÓN CORREGIDA - debe estar al mismo nivel
    fun actualizarDatos(nuevosClientes: List<Any>) {
        this.clientes = nuevosClientes
        this.clienteSeleccionado = null
        notifyDataSetChanged()
    }

    fun getClienteSeleccionado(): Any? = clienteSeleccionado
}