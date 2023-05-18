package com.example.telegram.ui.fragments.message_recycle_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view) {

    val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_image_message)
    val chatUserImageMessage: ImageView = view.findViewById(R.id.chat_user_image)
    val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)


    val blockReceivingImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_image_message)
    val chatReceivingImageMessage: ImageView = view.findViewById(R.id.chat_receiving_image)
    val chatReceivingImageMessageTime: TextView = view.findViewById(R.id.chat_recived_image_message_time)
}