package com.example.telegram.ui.screense.groups

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.ui.message_recycle_view.view_holder.*
import com.example.telegram.ui.message_recycle_view.views.MessageView

class GroupChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()
    private var listHolders = mutableListOf<MessageHolder>()


    // Функции от recycleView в которых происходит обработка и вывод данных
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = listMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(listMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        (holder as MessageHolder).onAttach(listMessagesCache[holder.adapterPosition])
        listHolders.add(holder as MessageHolder)

    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        listHolders.remove(holder as MessageHolder)
        (holder as MessageHolder).onDettach()
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

    fun destroy() {
        listHolders.forEach {
            it.onDettach()
        }
    }
}
