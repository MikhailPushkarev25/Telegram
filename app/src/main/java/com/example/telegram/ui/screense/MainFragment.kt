package com.example.telegram.ui.screense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.databinding.FragmentChatBinding
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.hideKeyBoard

class MainFragment : Fragment() {
    lateinit var chat: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chat = FragmentChatBinding.inflate(inflater, container, false)
        return chat.root
    }
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.appDrawer.enableDriver()
        hideKeyBoard()
    }

}