package com.example.telegram.ui.screense.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentContactsBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screense.base.BaseFragment
import com.example.telegram.ui.screense.singleChat.SingleChatFragment
import com.example.telegram.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView


class ContactsFragment : BaseFragment() {
    lateinit var contacts: FragmentContactsBinding
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var refContacts: DatabaseReference
    private lateinit var refUsers: DatabaseReference
    private lateinit var refUsersListener: AppValueEventListener
    private  var mapListeners = hashMapOf<DatabaseReference,AppValueEventListener>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contacts = FragmentContactsBinding.inflate(inflater, container, false)
        return contacts.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecycleView()
    }

    //Функция для заполнения из базы тулбар в контактах
    private fun initRecycleView() {
        recycleView = contacts.contactsRecycleView
        refContacts = REF_DATA_BASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(refContacts, CommonModel::class.java)
            .build()
        adapter = object: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                refUsers = REF_DATA_BASE_ROOT.child(NODE_USERS).child(model.id)

                refUsersListener =  AppValueEventListener {
                    val contact = it.getCommonModel()
                    if (contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else holder.name.text = contact.fullname

                    holder.status.text = contact.state
                    holder.photo.downLoadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(SingleChatFragment(model)) }
                }

                refUsers.addValueEventListener(refUsersListener)
                mapListeners[refUsers] = refUsersListener
            }

        }

        recycleView.adapter = adapter
        adapter.startListening()
    }

    class ContactsHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)

    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }
    }
}




