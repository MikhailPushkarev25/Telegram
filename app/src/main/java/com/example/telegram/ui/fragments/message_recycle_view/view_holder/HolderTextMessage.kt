package com.example.telegram.ui.fragments.message_recycle_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view) {

    val blockUserMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_message)
    val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)


    val blockReceivingMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_user_message)
    val chatReceivingMessage: TextView = view.findViewById(R.id.chat_recived_message)
    val chatReceivingMessageTime: TextView = view.findViewById(R.id.chat_recived_message_time)
}