package com.example.telegram.ui.message_recycle_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.UID
import com.example.telegram.ui.message_recycle_view.views.MessageView
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.downLoadAndSetImage

class HolderImageMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_image_message)
    private val chatUserImageMessage: ImageView = view.findViewById(R.id.chat_user_image)
    private val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)


    private val blockReceivingImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_image_message)
    private val chatReceivingImageMessage: ImageView = view.findViewById(R.id.chat_receiving_image)
    private val chatReceivingImageMessageTime: TextView = view.findViewById(R.id.chat_recived_image_message_time)


    //Функция отрисовывает картинки
    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserImageMessage.visibility = View.VISIBLE
            blockReceivingImageMessage.visibility = View.GONE
            chatUserImageMessage.downLoadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view.timeStamp.asTime()
        } else {
            blockUserImageMessage.visibility = View.GONE
            blockReceivingImageMessage.visibility = View.VISIBLE
            chatReceivingImageMessage.downLoadAndSetImage(view.fileUrl)
            chatReceivingImageMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDettach() {

    }
}