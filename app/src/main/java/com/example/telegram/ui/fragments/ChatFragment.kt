package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChatBinding

class ChatFragment : Fragment() {
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

    }

}