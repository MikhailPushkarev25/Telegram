package com.example.telegram.ui.screense.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.TYPE_CHAT
import com.example.telegram.database.TYPE_GROUP
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screense.groups.GroupChatFragment
import com.example.telegram.ui.screense.singleChat.SingleChatFragment
import com.example.telegram.utilits.downLoadAndSetImage
import com.example.telegram.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView

class MainListAdapter: RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.main_list_name)
        val itemLastMessage: TextView = view.findViewById(R.id.main_list_last_message)
        val itemPhoto: CircleImageView = view.findViewById(R.id.main_list_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder = MainListHolder(view)
        holder.itemView.setOnClickListener {
            when(listItems[holder.adapterPosition].type) {

                TYPE_CHAT -> replaceFragment(SingleChatFragment(listItems[holder.adapterPosition]))
                TYPE_GROUP ->  replaceFragment(GroupChatFragment(listItems[holder.adapterPosition]))
        }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downLoadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}