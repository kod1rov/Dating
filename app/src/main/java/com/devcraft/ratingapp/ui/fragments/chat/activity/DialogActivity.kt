package com.devcraft.ratingapp.ui.fragments.chat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.data.models.Message
import com.devcraft.ratingapp.ui.data.models.User
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dialog.*
import kotlinx.android.synthetic.main.fragment_hot_or_not.view.*

class DialogActivity : AppCompatActivity(R.layout.activity_dialog) {
    private  var user : User = User()
    private lateinit var uid : String
    private lateinit var FirebaseVM : FirebaseViewModel
    private  var listenerMesseges: ChildEventListener? = null
    private lateinit var listenerUser: ListenerRegistration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseVM = ViewModelProvider(this)[FirebaseViewModel::class.java]
        //get bundle data(images for match)
        var adapter = ChatAdapter()
        adapter.uid = FirebaseVM.uid.toString()
        name.text =  intent.getBundleExtra("user")?.get("name") as String
        uid =  intent.getBundleExtra("user")?.get("uid") as String
        var photo =  intent.getBundleExtra("user")?.get("photo") as String
        if(photo != ""){
            Picasso.get().load( intent.getBundleExtra("user")?.get("photo") as String).fit().into(photoDialog)
        }
        dialogList.adapter = adapter
        dialogList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        listenerUser =
                FirebaseVM.db.collection("users").document(uid).addSnapshotListener{ value, error->
                    if(error == null){
                        if (value != null) {
                            user.online = value.toObject(User::class.java)?.online
                        }
                        if(user.online == true){
                            status.visibility = View.VISIBLE
                        }else{
                            status.visibility = View.GONE
                        }
                    }
                }

        listenerMesseges = FirebaseVM.uid?.let {
            FirebaseVM.messagesRef.child(it).child(uid).orderByChild("timestamp").addChildEventListener(
                object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot.getValue(Message::class.java)?.let { it1 -> adapter.addItem(it1){
                            dialogList.smoothScrollToPosition(adapter.itemCount)
                        } }

                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
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

                }
            )
        }!!

        initListeners()
    }

    override fun onResume() {
        super.onResume()
        FirebaseVM.setStatus(true)

    }
    override fun onPause() {
        super.onPause()
        listenerUser.remove()
        user.uid?.let { FirebaseVM.uid?.let { it1 -> listenerMesseges?.let { it2 ->
            FirebaseVM.messagesRef.child(it1).child(it).removeEventListener(
                it2
            )
        } } }
    }

    private fun initListeners() {
        btn_back.setOnClickListener {
            finish()
        }
        btn_send_message.setOnClickListener {
            FirebaseVM.sendMessage(uid,Message(senderId = FirebaseVM.uid,text = messageText.text.toString(),timestamp = ServerValue.TIMESTAMP)) {
                messageText.text.clear()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}