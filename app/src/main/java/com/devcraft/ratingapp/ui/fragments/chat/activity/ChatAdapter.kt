package com.devcraft.ratingapp.ui.fragments.chat.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.data.models.Message
import com.devcraft.ratingapp.ui.data.models.User
import com.devcraft.ratingapp.ui.fragments.chat.ChatListAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item_dialog_list.view.*
import kotlinx.android.synthetic.main.item_message_currentuser_text.view.*
import kotlinx.android.synthetic.main.item_message_receiver_text.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatAdapter  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messages : MutableList<Message> = mutableListOf()
    var uid : String = ""
    class ReceiverMessageVH(view: View): RecyclerView.ViewHolder(view) {
        var text2 = view.id_message_current_user_text
    }

    class SenderMessageVH(view: View) : RecyclerView.ViewHolder(view){
        var text = view.id_message_receiver_text
    }

    override fun getItemViewType(position: Int): Int {

        return if(messages[position].senderId == uid){
                1
        }else{
            2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1 -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_message_receiver_text, parent, false)
                val holder = SenderMessageVH(view)
                return holder
            }
            2->{
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_message_currentuser_text, parent, false)
                val holder = ReceiverMessageVH(view)
                return holder
            }
            else -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_message_currentuser_text, parent, false)
                val holder = ReceiverMessageVH(view)
                return holder
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            1->{
                var viewHolder = holder as SenderMessageVH
                viewHolder.text.text = messages[position].text
            }
            2 ->{
                var viewHolder = holder as ReceiverMessageVH
                viewHolder.text2.text = messages[position].text
            }
        }

    }

    fun addItem(message: Message,onSuccess: () -> Unit){
        println(message)
        if(!messages.contains(message)){
            messages.add(message)
            println(messages)
            notifyItemInserted(messages.size)
            onSuccess()
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }


}