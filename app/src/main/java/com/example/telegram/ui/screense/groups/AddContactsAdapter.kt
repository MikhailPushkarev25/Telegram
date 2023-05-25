package com.example.telegram.ui.screense.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screense.singleChat.SingleChatFragment
import com.example.telegram.utilits.downLoadAndSetImage
import com.example.telegram.utilits.replaceFragment
import com.example.telegram.utilits.showToast
import de.hdodenhof.circleimageview.CircleImageView

class AddContactsAdapter: RecyclerView.Adapter<AddContactsAdapter.AddContactsHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class AddContactsHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.add_contacts_name)
        val itemLastMessage: TextView = view.findViewById(R.id.add_contacts_last_message)
        val itemPhoto: CircleImageView = view.findViewById(R.id.add_contacts_photo)
        val itemCheck: CircleImageView = view.findViewById(R.id.add_contacts_choice_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_item, parent, false)
        val holder = AddContactsHolder(view)
        holder.itemView.setOnClickListener {
            if (listItems[holder.adapterPosition].choice) {
                holder.itemCheck.visibility = View.INVISIBLE
                listItems[holder.adapterPosition].choice = false
                AddContactsFragment.listContacts.remove(listItems[holder.adapterPosition])
            } else {
                holder.itemCheck.visibility = View.VISIBLE
                listItems[holder.adapterPosition].choice = true
                AddContactsFragment.listContacts.add(listItems[holder.adapterPosition])
            }
        }
        return holder
    }

    override fun getItemCount(): Int = listItems.size

    override fun onBindViewHolder(holder: AddContactsHolder, position: Int) {
        holder.itemName.text = listItems[position].fullname
        holder.itemLastMessage.text = listItems[position].lastMessage
        holder.itemPhoto.downLoadAndSetImage(listItems[position].photoUrl)
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }
}