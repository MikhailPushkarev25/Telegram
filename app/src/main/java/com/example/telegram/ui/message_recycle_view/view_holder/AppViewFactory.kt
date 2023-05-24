package com.example.telegram.ui.message_recycle_view.view_holder

import com.example.telegram.database.TYPE_MESSAGE_FILE
import com.example.telegram.database.TYPE_MESSAGE_IMAGE
import com.example.telegram.database.TYPE_MESSAGE_VOICE
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.message_recycle_view.views.*

class AppViewFactory {

    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                TYPE_MESSAGE_IMAGE -> {
                    ViewImageMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl)
                }

                TYPE_MESSAGE_VOICE -> {
                    ViewvoiceMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text)
                }

                TYPE_MESSAGE_FILE -> {
                    ViewFileMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text)
                }
                else -> ViewTextMessage(message.id, message.from, message.timeStamp.toString(), message.fileUrl, message.text)
            }
        }
    }
}