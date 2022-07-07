package com.devcraft.ratingapp.ui.fragments.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.activity.bnv.BNVActivity
import com.devcraft.ratingapp.ui.data.models.User
import com.devcraft.ratingapp.ui.fragments.chat.activity.DialogActivity
import com.devcraft.ratingapp.ui.viewModels.FirebaseViewModel
import com.google.firebase.database.ChildEventListener
import kotlinx.android.synthetic.main.fragment_chat.*


interface OnItemClickListener {
    fun onItemClick(item: User?)
}

class ChatFragment : Fragment() {
    private var listnerChatFragment: ChildEventListener ? = null
    private var listenersChats: MutableList<ChildEventListener> = mutableListOf()
    private lateinit var FirebaseVM : FirebaseViewModel
    private lateinit var adapter : ChatListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseVM = ViewModelProvider(activity as BNVActivity)[FirebaseViewModel::class.java]
        adapter = ChatListAdapter { user ->
            var intent = Intent(context, DialogActivity::class.java)
            intent.putExtra(
                "user", bundleOf(
                    "name" to user.name,
                    "photo" to (user.gallery?.get(0) ?: ""),
                    "uid" to user.uid

                )
            )
            startActivity(intent)
            //println(user)
        }
        FirebaseVM.sortMessages()
        adapter.uid = FirebaseVM.uid
        adapter.lastMessages = FirebaseVM.lastMessages
        adapter.receiversData = FirebaseVM.userss.value!!
        adapter.receivers = FirebaseVM.receivers
        adapter.notifyDataSetChanged()
        FirebaseVM.callbackChatItem = {exist ,message->
            if(!exist){
                println("BBBBB $message")
                message.idReceiver?.let { adapter.receivers.add(it) }
                FirebaseVM.userss.value?.get(message.idReceiver)?.let { message.idReceiver?.let { it1 ->
                    adapter.receiversData.put(
                        it1, it)
                } }
                message.idReceiver?.let { adapter.lastMessages.put(it,message) }
                var newList = mutableListOf<String>()
                newList.add(message.idReceiver.toString())
                for (element in adapter.receivers){
                    if(element!=newList[0]){
                        newList.add(element)
                    }
                }
                adapter.receivers = newList
                adapter.notifyDataSetChanged()
            }else{
                println("AAAAAAA $message")
                var indexReceiver = FirebaseVM.receivers.indexOf(message.idReceiver)
                message.idReceiver?.let { adapter.lastMessages.put(it,message) }

                var newList = mutableListOf<String>()
                newList.add(message.idReceiver.toString())
                for (element in adapter.receivers){
                    if(element!=newList[0]){
                        newList.add(element)
                    }
                }
                adapter.receivers = newList
                adapter.notifyDataSetChanged()
            }
        }

        println(FirebaseVM.test)
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        super.onStart()
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.separator_rv_dialog_list)!!)
        rv_dialog_list.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        rv_dialog_list.adapter = adapter
        rv_dialog_list.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

    }


}