package com.example.telegram.ui.fragments.message_recycle_view.view_holder

import com.example.telegram.database.TYPE_MESSAGE_IMAGE
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.fragments.message_recycle_view.views.MessageView
import com.example.telegram.ui.fragments.message_recycle_view.views.ViewImageMessage
import com.example.telegram.ui.fragments.message_recycle_view.views.ViewTextMessage

class AppViewHolder {

    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                TYPE_MESSAGE_IMAGE -> {
                    ViewImageMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl)
                }
                else -> ViewTextMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text)
            }
        }
    }
}