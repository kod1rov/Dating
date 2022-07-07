package com.devcraft.ratingapp.ui.fragments.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devcraft.ratingapp.R
import com.devcraft.ratingapp.ui.data.models.Message
import com.devcraft.ratingapp.ui.data.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item_dialog_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatListAdapter(private val listener: (User) -> Unit) : RecyclerView.Adapter<ChatListAdapter.ChatListItemViewHolder>() {
    var uid: String? = ""
    var receivers: MutableList<String> = mutableListOf()
    var receiversData: HashMap<String, User> = hashMapOf()
    var lastMessages: HashMap<String, Message> = hashMapOf()

    class ChatListItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName = view.textView3
        val itemLastMessage = view.text
        val itemPhoto = view.userPhotoChatItem
        val time = view.time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item_dialog_list, parent, false)
        val holder = ChatListItemViewHolder(view)

        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {
        var receiver = receivers.elementAt(position)
        Picasso.get().load(receiversData[receiver]?.gallery?.get(0)).fit().into(holder.itemPhoto)//.fit().into(holder.itemPhoto)
        holder.itemName.text = receiversData[receiver]?.name

        if(lastMessages[receiver]?.senderId == uid){
            holder.itemLastMessage.text = lastMessages[receiver]?.text
        }
        else{
            holder.itemLastMessage.text = lastMessages[receiver]?.text
        }
        holder.time.text = lastMessages[receiver]?.timestamp.toString().asTime()
        holder.itemView.setOnClickListener { receiversData[receivers[position]]?.let { it1 ->
            listener(
                it1
            )
        } }

    }

    override fun getItemCount(): Int {
       return receivers.size
    }

    fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(time)
    }
}