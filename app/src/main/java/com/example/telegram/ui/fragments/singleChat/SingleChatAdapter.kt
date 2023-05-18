package com.example.telegram.ui.fragments.singleChat

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.UID
import com.example.telegram.ui.fragments.message_recycle_view.view_holder.AppHolderFactory
import com.example.telegram.ui.fragments.message_recycle_view.view_holder.HolderImageMessage
import com.example.telegram.ui.fragments.message_recycle_view.view_holder.HolderTextMessage
import com.example.telegram.ui.fragments.message_recycle_view.views.MessageView
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.downLoadAndSetImage

class SingleChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()


    // Функции от recycleView в которых происходит обработка и вывод данных
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = listMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HolderImageMessage -> drawMessageImage(holder, position)
            is HolderTextMessage -> drawMessageText(holder, position)
            else -> {}
        }
    }

    //Функция отрисовывает картинки
    private fun drawMessageImage(holder: HolderImageMessage, position: Int) {
        if (listMessagesCache[position].from == UID) {
            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.blockReceivingImageMessage.visibility = View.GONE
            holder.chatUserImageMessage.downLoadAndSetImage(listMessagesCache[position].fileUrl)
            holder.chatUserImageMessageTime.text = listMessagesCache[position].timeStamp.asTime()
        } else {
            holder.blockUserImageMessage.visibility = View.GONE
            holder.blockReceivingImageMessage.visibility = View.VISIBLE
            holder.chatReceivingImageMessage.downLoadAndSetImage(listMessagesCache[position].fileUrl)
            holder.chatReceivingImageMessageTime.text = listMessagesCache[position].timeStamp.asTime()
        }
    }
    //Функция отрисовывает текст
    private fun drawMessageText(holder: HolderTextMessage, position: Int) {
        if (listMessagesCache[position].from == UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivingMessage.visibility = View.GONE
            holder.chatUserMessage.text = listMessagesCache[position].text
            holder.chatUserMessageTime.text = listMessagesCache[position].timeStamp.asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivingMessage.visibility = View.VISIBLE
            holder.chatReceivingMessage.text = listMessagesCache[position].text
            holder.chatReceivingMessageTime.text = listMessagesCache[position].timeStamp.asTime()
        }
    }

    //Функция выполняет заполнение элементами лист вниз
    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }
        onSuccess()
    }

    //Функция выполняет заполнение элементами лист вверх
    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}
