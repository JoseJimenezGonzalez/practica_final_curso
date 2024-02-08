package com.example.myapplication.data.model

data class ReservarCarta(
    val idCarta: String = "",
    val nombreCarta: String = "",
    val nombreExpansion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val disponibilidad: String = "",
    val color: String = "",
    val urlImagenCarta: String = "",
    val idReserva: String = "",
    val idUsuario: String = "",
    val estado: String = ""
)
