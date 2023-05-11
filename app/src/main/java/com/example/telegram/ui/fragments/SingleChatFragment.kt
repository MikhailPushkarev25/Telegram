package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.telegram.R
import com.example.telegram.databinding.FragmentSingleChatBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.models.User
import com.example.telegram.utilits.*
import com.google.firebase.database.DatabaseReference

class SingleChatFragment(private val contact: CommonModel) : BaseFragment() {
    lateinit var singleChat: FragmentSingleChatBinding
    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivengUser: User
    private lateinit var refUser: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        singleChat = FragmentSingleChatBinding.inflate(inflater, container, false)
        return singleChat.root
    }
    override fun onResume() {
        super.onResume()
        listenerInfoToolbar = AppValueEventListener {
            receivengUser = it.getUserModel()
            initInfoToolBar()
        }
        refUser = REF_DATA_BASE_ROOT.child(NODE_USERS).child(contact.id)
        refUser.addValueEventListener(listenerInfoToolbar)
    }

    private fun initInfoToolBar() {
        if (receivengUser.fullname.isEmpty()) {
            singleChat.contactChatFullname.text = contact.fullname
        } else  singleChat.contactChatFullname.text = receivengUser.fullname
        singleChat.toolbarChatImage.downLoadAndSetImage(receivengUser.photoUrl)
        singleChat.contactChatStatus.text = receivengUser.state
    }

    override fun onPause() {
        super.onPause()
        refUser.removeEventListener(listenerInfoToolbar)
    }
}