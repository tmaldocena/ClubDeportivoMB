package com.example.clubdeportivomb.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*

object FileUtils {

    // Crear directorio para certificados si no existe
    private fun getCertificadosDirectory(context: Context): File {
        val directory = File(context.filesDir, "certificados")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    // Guardar archivo desde Uri al almacenamiento interno
    fun guardarCertificado(context: Context, uri: Uri, nombreArchivo: String): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputFile = File(getCertificadosDirectory(context), nombreArchivo)
            val outputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("FileUtils", "Certificado guardado: ${outputFile.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("FileUtils", "Error al guardar certificado", e)
            false
        }
    }

    // Obtener Uri del certificado guardado
    fun obtenerCertificado(context: Context, nombreArchivo: String): Uri? {
        return try {
            val file = File(getCertificadosDirectory(context), nombreArchivo)
            if (file.exists()) {
                Uri.fromFile(file)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Error al obtener certificado", e)
            null
        }
    }

    // Verificar si existe un certificado
    fun existeCertificado(context: Context, nombreArchivo: String): Boolean {
        return File(getCertificadosDirectory(context), nombreArchivo).exists()
    }

    // Obtener todos los certificados de un socio (por si tiene múltiples)
    fun listarCertificadosSocio(context: Context, dni: String): List<String> {
        val directory = getCertificadosDirectory(context)
        return directory.listFiles { file ->
            file.name.startsWith("certificado_$dni")
        }?.map { it.name } ?: emptyList()
    }

    // ✅ AÑADIR ESTA FUNCIÓN PARA ELIMINAR CERTIFICADOS
    fun eliminarCertificado(context: Context, nombreArchivo: String): Boolean {
        return try {
            val file = File(getCertificadosDirectory(context), nombreArchivo)
            if (file.exists()) {
                val eliminado = file.delete()
                if (eliminado) {
                    Log.d("FileUtils", "Certificado eliminado: $nombreArchivo")
                } else {
                    Log.e("FileUtils", "No se pudo eliminar el certificado: $nombreArchivo")
                }
                eliminado
            } else {
                Log.w("FileUtils", "Certificado no encontrado para eliminar: $nombreArchivo")
                false
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Error al eliminar certificado", e)
            false
        }
    }

    // ✅ FUNCIÓN ADICIONAL: Limpiar certificados antiguos de un socio
    fun limpiarCertificadosAntiguos(context: Context, dni: String, mantenerArchivo: String? = null) {
        try {
            val certificados = listarCertificadosSocio(context, dni)
            certificados.forEach { certificado ->
                if (certificado != mantenerArchivo) {
                    eliminarCertificado(context, certificado)
                }
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Error al limpiar certificados antiguos", e)
        }
    }
}