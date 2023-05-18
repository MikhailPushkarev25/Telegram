package com.example.telegram.ui.fragments.message_recycle_view.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.ui.fragments.message_recycle_view.views.MessageView

class AppHolderFactory {

    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_image, parent, false)
                    HolderImageMessage(view)
                }
                else -> {
                        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_text, parent, false)
                        HolderTextMessage(view)
                }
            }
        }
    }
}