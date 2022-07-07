package com.devcraft.ratingapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devcraft.ratingapp.ui.data.models.Message
import com.devcraft.ratingapp.ui.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.collections.HashMap

class FirebaseViewModel : ViewModel() {


    var rtdb =
        Firebase.database("https://dating-bd860-default-rtdb.firebaseio.com/")
    var messagesRef = rtdb.getReference("chats")
    var auth = FirebaseAuth.getInstance()
    var db = Firebase.firestore
    var storage = Firebase.storage
    var uid = auth.currentUser?.uid


    var potentials = mutableListOf<String>()

    var userss: MutableLiveData<HashMap<String, User>> = MutableLiveData(hashMapOf())
    var user: MutableLiveData<User> = MutableLiveData(User())
    var potentialMatch: MutableLiveData<User> = MutableLiveData()
    var test = 0


    var listenerUserList: ListenerRegistration? = null
    var listenUser: ListenerRegistration? = null

    var listnerChatFragment: ChildEventListener? = null
    var listenersChats: MutableList<ValueEventListener> = mutableListOf()

    var receivers: MutableList<String> = mutableListOf()
    var lastMessages: HashMap<String, Message> = hashMapOf()

    var callbackChatItem: ((exist: Boolean, message: Message) -> Unit?)? = null


    fun setUserListListener() {
        listenerUserList = db.collection("users").addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            } else {
                if (uid != null) {
                    for (el in value!!.documentChanges) {
                        when (el.type) {
                            DocumentChange.Type.ADDED -> {
                                if (el.document.id != uid) {
                                    if (!potentials.contains(el.document.id)) {
                                        userss.value?.put(
                                            el.document.id,
                                            el.document.toObject(User::class.java)
                                        )
                                        potentials.add(el.document.id)
                                        userss.value = userss.value
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setListenerUser(uid: String) {

        if (listenUser == null) {
            listenUser =
                db.collection("users").document(uid).addSnapshotListener { documentSnapshot, _ ->
                    if (documentSnapshot != null) {
                        val us = documentSnapshot.toObject(User::class.java)
                        us?.uid = documentSnapshot.id
                        potentialMatch.value = us
                    }
                }
        }

    }

    fun removeListenerUser() {
        listenUser?.remove()
        listenUser = null
        potentialMatch.value = null
    }

    init {

        val settings = firestoreSettings {
            isPersistenceEnabled = false
        }
        db.firestoreSettings = settings
        if (uid != null) {
            db.collection("users").document(uid!!)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (documentSnapshot != null) {
                        if(documentSnapshot.exists()) {
                            user.value = documentSnapshot.toObject(User::class.java)!!
                        }
                    }else{
                        println(firebaseFirestoreException?.message.toString())}
                }
        }

    }

    fun setStatus(status: Boolean) {
        uid?.let { db.collection("users").document(it).update("online", status) }
    }

    fun setResult(field: String, uidM: String) {
        uid?.let { db.collection("users").document(it).update(field, FieldValue.arrayUnion(uidM)) }
    }

    fun sendMessage(receiver: String, message: Message, callback: () -> Unit) {
        val idMessage = messagesRef.push().key
        message.id = idMessage
        val childUpd = hashMapOf<String, Any>(
            "/${user.value?.uid!!}/$receiver/$idMessage" to message,
            "/$receiver/${user.value?.uid!!}/$idMessage" to message
        )

        messagesRef.updateChildren(childUpd).addOnSuccessListener {
            callback()
        }
    }


    fun setlistenersChats() {
        if (listnerChatFragment == null) {
            uid?.let {
                listnerChatFragment = messagesRef.child(it).addChildEventListener(object :
                    ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val key = snapshot.key
                        if (key != null) {
                            if (!receivers.contains(key)) {
                                receivers.add(key)
                            }
                        }
                        if (key != null) {
                            val listener =
                                messagesRef.child(it).child(key).orderByChild("timestamp")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot2: DataSnapshot) {
                                            test++
                                            if (snapshot2.children.count() > 0) {
                                                val message =
                                                    snapshot2.children.last().getMessageModel()
                                                message.idReceiver = key
                                                if (callbackChatItem != null) {
                                                    if (lastMessages.containsKey(key)) {

                                                        lastMessages[key] = message
                                                        callbackChatItem?.let { it1 ->
                                                            it1(
                                                                true,
                                                                message
                                                            )
                                                        }
                                                        return
                                                    } else {


                                                        lastMessages[key] = message
                                                        callbackChatItem?.let { it1 ->
                                                            it1(
                                                                false,
                                                                message
                                                            )
                                                        }
                                                        return
                                                    }
                                                }else{
                                                    lastMessages[key] = message
                                                }
                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // TODO("Not yet implemented")
                                        }

                                    })
                            listenersChats.add(listener)
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        //TODO("Not yet implemented")
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        //TODO("Not yet implemented")
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        //TODO("Not yet implemented")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //TODO("Not yet implemented")
                    }
                })
            }
        }
    }

    fun removeListenersChats(){
        listenersChats.clear()
        uid?.let { listnerChatFragment?.let { it1 -> messagesRef.child(it).removeEventListener(it1) } }
        listnerChatFragment =  null
    }

    fun sortMessages() {
        if (receivers.isNotEmpty()) {
            receivers = receivers.sortedByDescending {

                lastMessages[it]?.timestamp.toString().toLong()
            } as MutableList<String>
        }
    }

    fun DataSnapshot.getMessageModel(): Message =
        this.getValue(Message::class.java) ?: Message()
}

