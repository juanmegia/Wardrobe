package com.example.wardrobe.model

data class Outfit (
    var id: String? = null,
    var userId: String = "",
    val name: String = "",
    val style: String = "",
    val prenda1 : String= "",
    val prenda2 : String= "",
    val prenda3 : String="",
    val prenda4 : String= "",
    val url: String = ""
)

