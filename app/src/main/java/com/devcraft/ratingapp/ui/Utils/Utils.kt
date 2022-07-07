package com.devcraft.ratingapp.ui.Utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.soywiz.klock.DateTime
import java.util.*

class Utils {
    companion object {
        fun getAge(time: Long): String {

            val millis1 = DateTime(time)
            val millis2: DateTime = DateTime.now()
            var temp = millis2.year.year - millis1.year.year
            val age = if(millis1.date.dayOfYear < millis2.date.dayOfYear){
                temp
            }else{
                --temp
            }
            return age.toString()
        }
    }
}
