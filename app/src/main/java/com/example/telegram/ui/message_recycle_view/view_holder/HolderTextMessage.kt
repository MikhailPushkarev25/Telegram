package com.example.telegram.ui.message_recycle_view.view_holder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.UID
import com.example.telegram.ui.message_recycle_view.views.MessageView
import com.example.telegram.utilits.asTime

class HolderTextMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_message)
    private val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
    private val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)


    private val blockReceivingMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_user_message)
    private val chatReceivingMessage: TextView = view.findViewById(R.id.chat_recived_message)
    private val chatReceivingMessageTime: TextView = view.findViewById(R.id.chat_recived_message_time)


    //Функция отрисовывает текст
    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivingMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text = view.timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivingMessage.visibility = View.VISIBLE
            chatReceivingMessage.text = view.text
            chatReceivingMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDettach() {

    }
}