package com.example.telegram.ui.message_recycle_view.view_holder

import com.example.telegram.ui.message_recycle_view.views.MessageView

interface MessageHolder {

    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDettach()

}