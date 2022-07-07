package com.devcraft.ratingapp.ui.fragments.chat

import com.devcraft.ratingapp.ui.data.models.Chat
import com.devcraft.ratingapp.ui.data.models.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue

val ChatChildEventListener = object : ChildEventListener {

    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
        //Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

        println(dataSnapshot.key)
        //val chat = Chat()
        //?.let { chat.messages?.add(it) }
        // A new comment has been added, add it to the displayed list
        //val comment = dataSnapshot.getValue<Comment>()

        // ...
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        //Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

        // A comment has changed, use the key to determine if we are displaying this
        // comment and if so displayed the changed comment.
        /*val newComment = dataSnapshot.getValue<Comment>()
        val commentKey = dataSnapshot.key*/

        // ...
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }


    override fun onCancelled(databaseError: DatabaseError) {
        println("postComments:onCancelled")
        /*Toast.makeText(context, "Failed to load comments.",
            Toast.LENGTH_SHORT).show()*/
    }
}

