package com.devcraft.ratingapp.ui.data.models

data class Message(
    var id: String? = "",
    var idReceiver : String? = "",
    var senderId : String? = "",
    var text : String? = "",
    var timestamp: Any? = ""
){
    override fun equals(other: Any?): Boolean {
        return (other as Message).id == id
    }
}
