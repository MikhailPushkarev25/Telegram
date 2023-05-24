package com.example.telegram.ui.message_recycle_view.views

data class ViewvoiceMessage(
    override var id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = ""
) : MessageView {

    override fun getTypeView(): Int{
        return MessageView.MESSAGE_VOICE
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}