package com.devcraft.ratingapp.ui.data.models

data class DataReg(
    var bornDate: Long? = 0,
    var desc: String? = "",
    var email: String? = "",
    var gallery:MutableList<String>  ? = mutableListOf(),
    var gender: String? = "",
    var hot: MutableList<String> ? = mutableListOf(),
    var matches: MutableList<String> ? = mutableListOf(),
    var not : MutableList<String> ? = mutableListOf(),
    var uid: String? = "",
    var name: String? = "",
    var online: Boolean? = false
)
